scalaVersion := "2.11.8"

enablePlugins(AndroidApp)
android.useSupportVectors

versionCode := Some(1)
version := "0.1-SNAPSHOT"

instrumentTestRunner :=
  "android.support.test.runner.AndroidJUnitRunner"

platformTarget := "android-25"

minSdkVersion := "23"

javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

proguardOptions ++= Seq(
  "-dontwarn okio.**",
  "-dontwarn okhttp3.**",
  "-dontwarn com.android.volley.**"
)

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.android.support" % "appcompat-v7" % "24.0.0",
  "com.android.support.test" % "runner" % "0.5" % "androidTest",
  "com.android.support.test.espresso" % "espresso-core" % "2.2.2" % "androidTest",

  "com.android.volley" % "volley" % "1.0.0",
  "com.squareup.okhttp3" % "okhttp" % "3.8.1",
  "com.nostra13.universalimageloader" % "universal-image-loader" % "1.9.5"
)

lazy val root = (project in file(".")).dependsOn(droidstar)

lazy val droidstar = project in file("droidstar")
