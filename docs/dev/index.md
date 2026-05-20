# Developer Guide

## CIVL Developer Dashboard

| Module | Source | JUnit | Coverage | Javadoc |
|--------|--------|-------|----------|---------|
| SARL | [Source](https://vsl.cis.udel.edu/trac/civl/browser/CIVL/mods/dev.civl.sarl) | [JUnit](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/sarl/junit) | [Coverage](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/sarl/coverage) | [Javadoc](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/sarl/javadoc) |
| GMC | [Source](https://vsl.cis.udel.edu/trac/civl/browser/CIVL/mods/dev.civl.gmc) | [JUnit](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/gmc/junit) | [Coverage](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/gmc/coverage) | [Javadoc](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/gmc/javadoc) |
| ABC | [Source](https://vsl.cis.udel.edu/trac/civl/browser/CIVL/mods/dev.civl.abc) | [JUnit](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/abc/junit) | [Coverage](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/abc/coverage) | [Javadoc](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/abc/javadoc) |
| MC | [Source](https://vsl.cis.udel.edu/trac/civl/browser/CIVL/mods/dev.civl.mc) | [JUnit](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/mc/junit) | [Coverage](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/mc/coverage) | [Javadoc](http://vsl.cis.udel.edu/lib/sw/civl/main/latest/mods/mc/javadoc) |

## Useful Links

* [Latest Main Branch Report](https://vsl.cis.udel.edu/lib/sw/civl/main/latest/)
* [All Main Branch Reports](https://vsl.cis.udel.edu/lib/sw/civl/main/)
* [All Branch Reports](https://vsl.cis.udel.edu/lib/sw/civl/)
* [User Page](../index.md)
* [wiki:PFG] Parallel Flow Graph low-level IR
* [PIL: Parallel Intermediate Language high-level IR](pil.md)

## Tool Development
* [wiki:"Be a CIVL developer"]
* How To
    * [wiki:"Add a command line option"]
* Analysis
    * [wiki:Overview]
    * [wiki:MemoryAnalysis]
    * [wiki:PointsToAnalysis]
    * [wiki:AliasAnalysis]
* Performance
    * [wiki:IdeasForPerformance]
    * [wiki:HeapCanonicalization]
    * [wiki:PolynomialExpansion]
* Coding standards
    * [wiki:"Coding Standards"]
    * [wiki:"Coding Standards for CIVL models"]
* Comparison
    * [wiki:Comparison]
* Fortran Translation
    * [wiki:FortranOverview]
    * [wiki:FortranTransformations]
    * [wiki:FortranTranslationIssues]
* CUDA Translation
    * [[wiki:Implementation_of_CUDA_in_CIVL|Implementation of CUDA in CIVL]]
    * [[wiki:Notes_on_CUDA_Semantics|Notes on CUDA Semantics]]
* Transformers
    * [wiki:GeneralTransformation]: translates away arguments of main.
    * [wiki:IOTransformation]: translates stdio.h-related code to fit CIVL's stdio implementation.
    * [wiki:MPITransformation]: translates MPI to CIVL.
    * [wiki:PthreadTransformation]: translates Pthread code to CIVL code.
    * [wiki:OpenMPTransformation]: translates OpenMP to CIVL.
    * [wiki:Next-GenOpenMPTransformation]: another project with different approaches for translating OpenMP to CIVL.
    * [wiki:OpenCLTransformation]: translates OpenCL to CIVL.
* CIVL pragma: [wiki:CIVLPragmas]
* GUI
    * [wiki:GUIRequirements]
    * [wiki:GUIDesign]
    * [wiki:TraceViewer]
* [wiki:CIVLite]


## Related Tools

These are links to wiki-pages or official sites of tools used by CIVL
* [wiki:ABC]
* [wiki:GMC]
* [wiki:SARL]
* Z3 (https://github.com/Z3Prover/z3/wiki)
* CVC4 (http://cvc4.cs.stanford.edu/web/)
* Why3 (http://why3.lri.fr/)
* Frama-C (https://frama-c.com/)

## Other Links
* Paper reading:
  * Sources <https://vsl.cis.udel.edu/readings.html>
  * Schedule <https://docs.google.com/spreadsheet/ccc?key=0AvyY9XPxT2MVdFJzMThfWVdGZFpsYkNCcEJzUGdyYWc#gid=0>
* [wiki:Conferences]
* [wiki:CIVL-C Wishlist]

## Deprecated Pages

Pages are going to be deleted.

* [wiki:Insieme]
* [wiki:BattleOfTheDialects]
* [wiki:2018_06_28] Control Flow Graph
* [wiki:2018_07_05] C Implementation of Naive Decision Tree
* [wiki:IntDivOperations]
* [wiki:Changes2023]
* [wiki:IR] : CIVL-IR (old)
* [wiki:IR2] : CIVL-IR (new)
* [wiki:LAST]: Low-level AST
* [wiki:DataStructures]
* [wiki:Arrays]
* [wiki:Pointers]
* [wiki:Choose]
* [wiki:MessagePassing]
* [wiki:OmnibusChanges]
* [wiki:ContractReduction]
* [wiki:VerificationWithContracts]
* [wiki:CommonHelperFunctionsForDifferentParallelLanguage]
