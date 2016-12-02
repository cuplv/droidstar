{ stdenv, texlive }:

let

latex = texlive.combine { inherit (texlive)
  beamer
;};

# latex = texlive.beamer;

in

stdenv.mkDerivation rec {
  name = "droid-star-presentation";
  src = ./.;
  buildInputs = [ latex ];
  buildPhase = ''
    pdflatex *.tex
  '';
  installPhase = ''
    mkdir $out
    cp -v *.pdf $out/
  '';
}
