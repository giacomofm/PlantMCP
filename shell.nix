{ pkgs ? import <nixpkgs> {} }:
let
  java-package = pkgs.javaPackages.compiler.temurin-bin.jdk-25;
in
  pkgs.mkShellNoCC {
    packages = with pkgs; [
      java-package
      maven
    ];

    shellHook = ''
      JAVA_HOME=${java-package}
      echo "❄️ NixShell is ready"
      echo "☕ Java $(java --version)"
      echo "📦 Maven $(mvn --version)"
    '';
  }