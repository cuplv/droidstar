with (import <nixpkgs> {});

let

sbt-android = stdenv.mkDerivation {
  name = "sbt-android";
  src = ./.;
  buildInputs = [ makeWrapper ];
  propagatedBuildInputs = [ androidsdk ];
  installPhase = ''
    mkdir -p $out/bin
    makeWrapper ${sbt}/bin/sbt $out/bin/sbt \
      --set ANDROID_HOME "${androidsdk}/libexec"
  '';
};

in

stdenv.mkDerivation {
  name = "droid-star";
  src = ./.;
  buildInputs = [ sbt-android graphviz ];
}
