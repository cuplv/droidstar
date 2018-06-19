lazy val meta = Seq(
  name := "droidstar",
  version := "0.1.2.1"
)

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  platformTarget in Android := "android-26",
  minSdkVersion in Android := "23",
  showSdkProgress in Android := false,
  libraryDependencies ++= commonDeps
) ++ meta

lazy val commonDeps = Seq(
  "com.android.support" % "appcompat-v7" % "24.0.0",
  "com.android.support.test" % "runner" % "0.5" % "androidTest",
  "com.android.support.test.espresso" % "espresso-core" % "2.2.2" % "androidTest"
)

lazy val root = (project in file (".")).aggregate(driverApp,lib).settings(meta)

lazy val driverApp = Project(id = "driverApp", base = file("driver-app"))
  .enablePlugins(AndroidApp)
  .dependsOn(lib)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      // "com.android.volley" % "volley" % "1.1.0",
      "com.squareup.okhttp3" % "okhttp" % "3.8.1",
      "com.nostra13.universalimageloader" % "universal-image-loader" % "1.9.5"
    ),
    proguardOptions in Android ++= Seq(
      "-dontwarn okio.**",
      "-dontwarn okhttp3.**",
      "-dontwarn com.android.volley.**"
    )
  )

lazy val lib = Project(id = "lib", base = file("lib"))
  .enablePlugins(AndroidJar)
  .settings(
    commonSettings,
    exportJars := true
  )
