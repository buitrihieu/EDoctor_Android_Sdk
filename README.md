**Các bước tích hợp:**

**Update file build.gradle project **

```kotlin
allprojects {
  repositories {
    google()
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```

- **Update file build.gradle module **

```java
dependencies {
...
    implementation 'com.github.buitrihieu:EDoctor_Android_Sdk:0.1.0'
...
}
```

# Cách sử dụng SDK:

### Khởi tạo PayME SDK:

Trước khi sử dụng EDoctor SDK, cần gọi phương thức khởi tạo một lần duy nhất để khởi tạo SDK.

```kotlin
import vn.edoctor.sdk_edoctor.EDoctor
val  eDoctor = EDoctor(context, supportFragmentManager)
```

### Mở webview:
Mở webview để load web của EDoctor

```kotlin
eDoctor?.openWebView(onSuccess = {
    Log.d(EDoctor.LOG_TAG, "onSuccess")
}, onError = { _, _, s ->
    EDoctor.showError(s)
})
```

