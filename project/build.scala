import sbt._
import sbt.Keys._
import android.Dependencies.LibraryProject
import android.{AndroidApp,AndroidJar}
import android.Keys._

object MyProjectBuild extends Build {

  lazy val driverApp = Project(id = "driverApp", base = file("driver-app"))
    .enablePlugins(AndroidApp)
    .dependsOn(lib)
    .settings(appSettings: _*)
    .settings(libraryDependencies ++= Seq(
      "com.android.support" % "appcompat-v7" % "24.0.0",
      "com.android.support.test" % "runner" % "0.5" % "androidTest",
      "com.android.support.test.espresso" % "espresso-core" % "2.2.2" % "androidTest",
    
      "com.android.volley" % "volley" % "1.0.0",
      "com.squareup.okhttp3" % "okhttp" % "3.8.1",
      "com.nostra13.universalimageloader" % "universal-image-loader" % "1.9.5"
    ))

  val lib = Project(
    id = "lib",
    base = file("lib"))
      .enablePlugins(AndroidJar)
      // .settings(android.Plugin.androidBuildJar: _*)
      .settings(
        libraryDependencies ++= Seq(
          "com.android.support" % "appcompat-v7" % "24.0.0",
          "com.android.support.test" % "runner" % "0.5" % "androidTest",
          "com.android.support.test.espresso" % "espresso-core" % "2.2.2" % "androidTest"
        ),
        platformTarget in Android := "android-26",
        minSdkVersion in Android := "23",
        exportJars := true
      )

  lazy val appSettings = List(
    proguardOptions in Android ++= Seq(
      "-dontwarn okio.**",
      "-dontwarn okhttp3.**",
      "-dontwarn com.android.volley.**"
    ),
    platformTarget in Android := "android-26",
    minSdkVersion in Android := "23"
  )
}
