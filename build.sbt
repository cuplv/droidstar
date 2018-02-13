scalaVersion := "2.11.8"

enablePlugins(AndroidJar)
useSupportVectors

versionCode := Some(1)
version := "0.1"

instrumentTestRunner :=
  "android.support.test.runner.AndroidJUnitRunner"

javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

libraryDependencies ++= Seq(
  "com.android.support" % "appcompat-v7" % "24.0.0",
  "com.android.support.test" % "runner" % "0.5" % "androidTest",
  "com.android.support.test.espresso" % "espresso-core" % "2.2.2" % "androidTest"
)

platformTarget := "android-26"
