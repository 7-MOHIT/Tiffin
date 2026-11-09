# Tiffin — Daily Kitchen Subscription (Internship Assignment)

## What I Cut and Why

> *"An honest cut beats a half-finished screen every time."*

In scoping this 4-hour build, I prioritized an installable, rock-solid app that strictly satisfies all core requirements (error/empty/loading states, process death survival, persistence via DataStore, navigation, and analytics instrumentation). Here is what was deliberately cut:

1. **Live Payment Gateway (Razorpay/Stripe SDK)**: 
   - *Why cut*: The prompt explicitly asked to fake the purchase without a payment SDK. Integrating a mock gateway sheet would add UI overhead without changing the verification outcome. I focused instead on rock-solid persistence using Jetpack Preferences DataStore and testing the exact lifecycle conditions (surviving rotation, force-stop, and process recreation).
2. **Complex Multi-Filter & Search UI**:
   - *Why cut*: Instead of spending time on a complex search bar with multi-tag filtering (distance sliders, price brackets, multiple cuisines), I included a single clean "Pure Veg" toggle filter and focused heavily on high-fidelity card design, typography, spacing, and verified loading/empty/error states.
3. **Remote Backend / Real-time API**:
   - *Why cut*: Bound strictly to bundled JSON as requested (`kitchens.json` with 12 diverse kitchens). Rather than setting up a mock HTTP server, I invested in a robust `KitchenRepository` with coroutine-based asynchronous loading, simulated latency (600ms), and error recovery mechanisms.

### Product Feature Highlight: Freemium Weekly Menu Gating
As specified in the product brief (*"free users browse; paid users get delivery slots and the weekly menu"*):
- **Free Users**: Monday's menu is open as a free teaser, while Tuesday through Sunday cards are locked with a `PLUS ONLY` badge and direct paywall click-through.
- **Paid Users**: Instant unlocking of all 7 days of rotating home-cooked meals upon subscribing.

---

## Task 2 — Fix What the Agent Wrote

### Original Code:
```kotlin
@Composable
fun KitchenList(vm: KitchenViewModel) {
    var items = mutableListOf<Kitchen>()
    LaunchedEffect(items) {
        val res = URL(BASE + "?key=" + "sk_live_9f2c41ab")
            .readText()
        items.addAll(parse(res))
    }
    LazyColumn { items(items) { KitchenRow(it) } }
}
```

### The Four Problems:

1. **Security & Financial Liability (Gets us a massive bill)**:
   - *What breaks*: The live secret key (`"sk_live_9f2c41ab"`) is hardcoded directly into client source code and sent over a query string. Anyone decompiling the APK or inspecting client traffic can extract this key, hijack our API quota, and exhaust paid services, generating huge unexpected bills.
2. **Blocking Network Call on the Main Thread (Gets us 1-star reviews)**:
   - *What breaks*: `URL(...).readText()` performs synchronous network I/O directly inside `LaunchedEffect` without switching to `Dispatchers.IO`. In Android, this throws `NetworkOnMainThreadException` on modern SDKs or hangs the UI thread, causing ANRs (Application Not Responding) and immediate app freezes, leading to 1-star Play Store reviews.
3. **Broken Recomposition & Infinite Loop on State**:
   - *What breaks*: `var items = mutableListOf<Kitchen>()` is a plain JVM list recreated on every recomposition. Modifying it with `items.addAll(...)` does **not** notify Compose to trigger a recomposition. Furthermore, passing `items` as the key to `LaunchedEffect(items)` causes it to re-execute infinitely every time `items` is allocated, while completely ignoring the passed `vm: KitchenViewModel`.
4. **Missing Lifecycle Awareness, Error Handling, and Architecture Layering**:
   - *What breaks*: Network requests are coupled directly to the UI composable with no `try-catch`, no loading spinner, and no error states. If the screen is rotated or closed while loading, requests leak or crash unhandled. Data fetching belongs in the `ViewModel` using coroutine scopes and unidirectional data flow (`StateFlow`).

### Corrected Version:

```kotlin
// KitchenUiState.kt
sealed interface KitchenUiState {
    data object Loading : KitchenUiState
    data object Empty : KitchenUiState
    data class Error(val message: String) : KitchenUiState
    data class Success(val kitchens: List<Kitchen>) : KitchenUiState
}

// KitchenViewModel.kt
class KitchenViewModel(
    private val repository: KitchenRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<KitchenUiState>(KitchenUiState.Loading)
    val uiState: StateFlow<KitchenUiState> = _uiState.asStateFlow()

    init {
        loadKitchens()
    }

    fun loadKitchens() {
        viewModelScope.launch {
            _uiState.value = KitchenUiState.Loading
            repository.fetchKitchens()
                .onSuccess { list ->
                    _uiState.value = if (list.isEmpty()) KitchenUiState.Empty else KitchenUiState.Success(list)
                }
                .onFailure { error ->
                    _uiState.value = KitchenUiState.Error(error.localizedMessage ?: "Failed to load kitchens")
                }
        }
    }
}

// KitchenList.kt
@Composable
fun KitchenList(
    viewModel: KitchenViewModel,
    onKitchenClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is KitchenUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is KitchenUiState.Empty -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No kitchens available near you.")
            }
        }
        is KitchenUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = viewModel::loadKitchens) {
                        Text("Retry")
                    }
                }
            }
        }
        is KitchenUiState.Success -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.kitchens, key = { it.id }) { kitchen ->
                    KitchenRow(kitchen = kitchen, onClick = { onKitchenClick(kitchen.id) })
                }
            }
        }
    }
}
```

---

## Task 3 — The Release Note

### 1. Manual Testing Matrix & Target Devices
Before pressing Publish on the Play Console, I would execute the following manual tests:
- **Test Scenarios**:
  1. *Fresh Install & Launch Funnel*: Verify cold launch count increments correctly across launches 1, 2, and 3. Ensure the paywall modal automatically appears on launch 3.
  2. *Paywall & Purchase Flow*: Tap "Start for ₹1", verify confirmation toast/state change, confirm paywall dismisses and never appears again on subsequent launches (4th, 5th, etc.) or subscribe button clicks.
  3. *Process Death & Rotation*: Rotate device on List and Detail screens; confirm scroll position and state remain intact. Force-stop the app via `adb shell am force-stop com.example.tiffin`, relaunch, and verify that paid subscription status persisted across processes.
  4. *Offline/Degraded Network*: Load without assets/network; verify that Error and Empty states render user-friendly retry buttons rather than frozen screens.
- **Device Coverage Matrix**:
  - *Budget / Low-end*: Redmi 9A / Samsung Galaxy A13 (Android 11/12, 2GB–3GB RAM) — verifies memory pressure, lazy column scrolling, and low-RAM process termination.
  - *Mid-tier*: Pixel 6a / Nothing Phone (1) (Android 13/14) — verifies standard Material 3 dynamic styling and navigation gestures.
  - *Flagship*: Samsung Galaxy S24 Ultra / Pixel 9 Pro (Android 14/15, 120Hz) — verifies high refresh rate rendering and edge-to-edge system insets.
  - *Small screen*: Pixel 4a or 5.0" HD device — checks text truncation on pricing and weekly menu items.

### 2. The One Thing Most Likely to Break in Production & Proactive Detection
- **What will break**: **Preferences DataStore corruption or race condition during rapid app launches / process kills**. If DataStore encounters an I/O read timeout or serialization failure on older devices with slow flash storage, the app might fallback to `paid = false`, improperly showing the paywall to a paying subscriber.
- **Proactive Detection (Without user complaints)**:
  - Track `paywall_shown` events tagged with `user_subscription_status: active`.
  - Configure an alert in Firebase Crashlytics / Datadog: if any event triggers with `trigger_source == "auto_3rd_launch"` where local or remote user record already has `is_paid == true`, raise a **P0 alert**.
  - Monitor non-fatal exceptions in Crashlytics filtering for `IOException` under `androidx.datastore`.

### 3. If It Breaks at 11 PM: Rollback Strategy
1. **Immediate Play Console Action**: If the rollout is staged (e.g., 10%–20%), immediately **Halt Rollout** in the Play Console to stop new downloads.
2. **Server-side Kill Switch**: If feature flag / remote config is enabled, flip `paywall_force_disabled = true` to prevent any paywall display while allowing free browsing.
3. **Hotfix / Rapid Rollback Release**: Revert the offending commit on `main`, bump the `versionCode` (e.g., from `100` to `101`), build and sign a clean release bundle, and submit to Play Console under an expedited review track.

### 4. Next Three Things to Build (In Priority Order)
1. **Real Payment Gateway & Billing Integration (Google Play Billing / Razorpay)**: Replace mock payment with Google Play Billing Library 7.x, handling active subscriptions, grace periods, billing retries, and receipt verification on a backend.
2. **Cart, Delivery Slot Selection & Kitchen Ordering Flow**: Allow users to select specific days of the week, choose lunch vs dinner delivery slots, and place their meal delivery orders directly.
3. **Live Order Tracking & Push Notifications**: Real-time delivery status updates using FCM (Firebase Cloud Messaging) to notify users when food is prepared, dispatched, and delivered.

---

## Task 4 — How I Used AI

### 1. Tools Used & Code Share
- **Tools**: Gemini 3.8 Flash / Claude via Antigravity Agentic IDE.
- **Estimated Code Share**: ~70% generated/assisted by AI (boilerplate ViewModel setup, initial Compose templates, data model classes), ~30% written, architected, debugged, and refined by hand.

### 2. Single Best Prompt
> *"Analyze the `KitchenDetailsScreen` and compare the day abbreviations used in Compose (`Mon`, `Tue`) against the actual keys in `kitchens.json`. Why are meal descriptions falling back to 'Menu not available', and how can we support both without breaking existing data contracts?"*

### 3. One Thing the Agent Got Wrong & How I Caught It
- **The Bug**: When generating the weekly menu in `KitchenDetailsScreen.kt`, the agent mapped days using a 3-letter abbreviation list (`"Mon"`, `"Tue"`, `"Wed"`), but the JSON asset `kitchens.json` stored full day names (`"Monday"`, `"Tuesday"`, etc.).
- **How I Caught It**: When running the app on the emulator and inspecting the detail view, every single day displayed *"Menu not available"*.
- **The Fix**: I inspected the asset file and updated the mapping in `KitchenDetailsScreen.kt` to defensively check both formats:
  ```kotlin
  val meal = kitchen.weeklyMenu[fullDayName]
      ?: kitchen.weeklyMenu[shortKey]
      ?: "Chef's Special Homestyle Thali"
  ```
  Additionally, the agent omitted a back button in `KitchenDetailsContent`, making the screen a dead-end with no way back to the home list. I manually added a standard Material 3 `TopAppBar` with back navigation.

### 4. One Thing Written by Hand Because Directing the Agent Was Slower
- **Designing the Paywall Trigger & DataStore State (`PaywallStore` & `AppNavHost` integration)**:
  Directing an agent through the subtle interaction of `launchCount >= 3`, preventing double increments on device rotation using `SavedStateHandle`, and ensuring `autoPaywallShown` is persisted so the modal only automatically interrupts once required precise state tracking that was faster to write and verify by hand than repeatedly prompting.

---

## Analytics Instrumentation (`Analytics.kt`)

Shipped with interface `Analytics` and `LogcatAnalytics` (tagged `TiffinAnalytics`).

### The Four Moments Selected:
1. `kitchen_list_loaded`: Logged when the home screen successfully renders kitchens (`kitchen_count`). Represents **Discovery (Top of Funnel)**.
2. `kitchen_detail_viewed`: Logged when a user selects a kitchen (`kitchen_id`, `kitchen_name`, `cuisine`, `price`). Represents **Consideration / Intent (Middle of Funnel)**.
3. `paywall_shown`: Logged whenever the paywall appears (`trigger_source`: `"auto_3rd_launch"` vs `"subscribe_button"`). Represents **Monetization Trigger (Bottom of Funnel)**.
4. `subscription_completed`: Logged when the user taps purchase on the paywall (`trial_price: 1`, `recurring_price: 249`). Represents **Conversion & Revenue (Core Business Outcome)**.

---

## How to Build & Install

### Prerequisites
- JDK 17 or higher (tested with JDK 25)
- Android SDK Platform 37

### Build Debug APK
```bash
./gradlew assembleDebug
```
The APK will be generated at:
```
app/build/intermediates/apk/debug/app-debug.apk
```

### Run Local Unit Tests
```bash
./gradlew testDebugUnitTest
```
