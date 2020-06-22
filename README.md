[ ![Download](https://api.bintray.com/packages/kevinchung0921/Maven/einvoice_sdk/images/download.svg?version=1.3) ](https://bintray.com/kevinchung0921/Maven/einvoice_sdk/1.3/link)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 電子發票 SDK
這是一個可以用來開發電子發票應用的SDK，實做了 [電子發票應用API規格 1.7 版](https://www.einvoice.nat.gov.tw/home/DownLoad?fileName=1510206773173_0.pdf)，提供實體發票/載具發票查詢，一維條碼/QR Code 資料解析功能。


![Demo App screen 1](https://drive.google.com/uc?export=view&id=1-2JvgN3avYLnTfmDb2kjp8DPrHg9eveD)

![Demo App screen 2](https://drive.google.com/uc?export=view&id=172hO_1-Q8v70Exp7RipYOTotTtvK7SXu)


## 使用方法：

### 添加dependency到build.gradle：

```groovy
dependencies {
    implementation 'com.kevinchung0921:einvoice:1.3'
}
```
### 初始化

初始化物件時，需帶入電子發票 API key，如果沒有的話可以到[這裡](https://www.einvoice.nat.gov.tw/APCONSUMER/BTC605W/)申請。
```kotlin
  val invSdk = EInvoice("API_KEY")
```


### 查詢載具發票表頭

查詢時間區間內的載具發票表頭(CarrierHeader)[https://github.com/kevinchung0921/einvoice_sdk/blob/master/einvoice/src/main/java/com/kevinchung/einvoice/data/CarrierHeader.kt]，需要設定要查詢的**載具條碼**以及**認證碼**，同時可以指定是否只顯示中獎發票

```kotlin
  val now = Date()
  // 查詢本月載具發票
  val monthStart = Date(now.year, now.month, 1).time
  val monthEnd = Date(now.year, now.month+1, 1).time -1
  // 回傳 List<CarrierHeader>，或是 null 如果發生錯誤
  val headers = invSdk.getCarrierHeader(monthStart, monthEnd, USER_BARCODE, USER_PASS, false)
  for(header in headers)
    Log.d(TAG,header.toString())
```

### 查詢載具發票內容
使用載具表頭物件來查詢載具發票的詳細內容 [InvoiceDetail](https://github.com/kevinchung0921/einvoice_sdk/blob/master/einvoice/src/main/java/com/kevinchung/einvoice/data/InvoiceDetail.kt)
```kotlin
  // 回傳 InvoiceDetail 物件，或是 null 如果發生錯誤
  val detail = invSdk.getCarrierDetail(header, USER_BARCODE, USER_PASS)
```

### 解析紙本發票條碼
取得紙本發票條碼內容 [Invoice](https://github.com/kevinchung0921/einvoice_sdk/blob/master/einvoice/src/main/java/com/kevinchung/einvoice/data/Invoice.kt)
```kotlin
  // 指定條碼格式，可以是QR Code(FORMAT_QR_CODE)或是一維條碼(FORMAT_CODE_39)
  // 成功的話回傳 Invoice 物件
  val invoice = Invoice(Const.FORMAT_QR_CODE, QRCODE_CONTENTS)
```
### 查詢紙本發票內容

```kotlin
  // 使用 Invoice 物件查詢內容
  val detail = invSdk.getInvDetail(invoice!!)
```
### 整合 zxing barcode scanner 來取得發票條碼
透過整合 zxing library，再加上幾行程式就可以輕鬆的取得紙本發票的條碼。
將 zxing 加入 depenencies 區塊
```groovy
  dependencies {
    implementation 'com.journeyapps:zxing-android-embedded:3.4.0'
  }
```
呼叫 zxing 範例
```kotlin
  fun startScan() {
    val integrator = IntentIntegrator(this)
    integrator.captureActivity = CaptureActivity::class.java
    val supportList: Collection<String> = listOf("QR_CODE", "CODE_39")
    integrator.setDesiredBarcodeFormats(supportList)
    integrator.setPrompt("請掃描電子發票左方的QR Code")
    integrator.setOrientationLocked(false)
    integrator.setBeepEnabled(true)
    integrator.initiateScan()
  }
```
覆寫 onActivityResult
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when(requestCode) {
        IntentIntegrator.REQUEST_CODE -> {
            // 先呼叫 zxing 的 parseActivityResult 來取得 IntentResult 物件
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if(result.contents == null)
                return
            // 先解析條碼內容取得 Invoice 物件    
            val invoice = Invoice(result.formatName, result.contents)
            // 取得發票內容
            einvSdk?.run {
                Thread {
                    Looper.prepare()
                    val detail = getInvDetail(invoice!!)
                    Log.d(TAG,detail.toString())

                }.start()
            }

        }
        else -> super.onActivityResult(requestCode, resultCode, data)
    }
}
```

### 注意事項
* 查詢發票內容以及載具發票表頭時，會使用 HTTP function，所以要注意不要在 UI thread 中直接呼叫，或是使用非同步版本 functioni
* CarrierHeader/InvoiceDetail 物件內有 **code** 欄位，需要檢查是否為成功(200)，如果不是的話就是有錯誤，可以檢查 **msg** 欄位看看是什麼錯誤。
