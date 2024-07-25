(define-module (mattilsynet smilefjes-deux)
  #:use-module ((nonguix licenses) #:prefix license:)
  #:use-module (git)
  #:use-module (gnu packages base)
  #:use-module (gnu packages java)
  #:use-module (gnu packages web)
  #:use-module (gnu packages certs)
  #:use-module (gnu packages clojure)
  #:use-module (gnu packages curl)
  #:use-module (gnu packages fonts)
  #:use-module (gnu packages node)
  #:use-module (gnu packages version-control)
  #:use-module (guix build-system clojure)
  #:use-module (guix git)
  #:use-module (guix git-download)
  #:use-module (guix packages))

;; versioning code taken from idris
;; ref https://raw.githubusercontent.com/idris-lang/Idris-dev/master/guix.scm

(define *source-dir* (dirname (current-filename)))

(define *include-worktree-changes* #f)

(define (latest-git-commit-hash dir)
  (with-repository dir repo
                   (oid->string (reference-target (repository-head repo)))))

(define (current-git-branch-name dir)
  (with-repository dir repo
                   (branch-name (repository-head repo))))

(define-public smilefjes-deux
  (package
    (name "smilefjes-deux")
    (version (git-version "0.0.0"
                          (string-append
                           (current-git-branch-name *source-dir*)
                           (if *include-worktree-changes*
                               "-dirty"
                               ""))
                          (latest-git-commit-hash *source-dir*)))
    (source (if *include-worktree-changes*
                (local-file *source-dir*
                            #:recursive #t
                            #:select? (git-predicate *source-dir*))
                (git-checkout (url *source-dir*)
                              (branch branch-name))))
    (build-system clojure-build-system)
    (arguments
     (list
      #:jdk openjdk))
    (inputs
     (list nss-certs
           gnu-make
           curl
           git
           node-lts
           clojure-tools
           font-awesome))
    (home-page "https://github.com/mattilsynet/smilefjes-deux")
    (synopsis "Smilefjes-plakater på nett")
    (description "Smilefjes-plakater på nett")
    (license (license:nonfree "https://github.com/mattilsynet/smilefjes-deux"
                              "All Rights Reserved"))))

smilefjes-deux
