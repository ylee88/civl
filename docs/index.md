# The CIVL Model Checker

## About

The CIVL model checker is a verification tool for sequential or parallel
programs.
It targets primarily C programs, including parallel C programs that use
OpenMP or MPI, and CUDA-C programs.
The general verification approach is based on model checking and symbolic execution.   

The verifier uses a concurrency intermediate verification language, CIVL-C.
Users may write in CIVL-C directly; this is a good way to prototype
algorithms.   One may also start from standard C (or CUDA-C), which
the tool first translates to CIVL-C.  CIVL may also be applied to programs 
composed of multiple translation units, some of which are in CIVL-C
and others in C or CUDA-C.

CIVL can verify a number of safety properties, including absence of assertion
violations, deadlocks, memory leaks, illegal pointer dereferences, out-of-bound
array indexes, and divisions by zero.  In most cases, this requires placing
relatively small bounds on the size of inputs and/or the numbers of threads or
processes.  In any case, when a violation is found, detailed diagnostic
information is provided, including a (minimal) execution trace terminating
in a violating state.

CIVL currently supports only subsets of the languages and language
extensions mentioned above.  We are continually expanding these subsets,
prioritizing the most commonly-used language features.


## Downloads & Links

* [Latest stable release](http://vsl.cis.udel.edu/lib/sw/civl/current/latest/)
* [Latest unstable release](http://vsl.cis.udel.edu/lib/sw/civl/trunk/latest/)
* [Older releases](http://vsl.cis.udel.edu/lib/sw/civl/)
* [CIVL Web App](http://civl.cis.udel.edu/app/)

## Documentation
* [Introduction](introduction.md)
* [CIVL-C Fundamentals](fundamentals.md)
* [Language Manual](language.md)
* [Libraries](libraries.md)
* [Command Line Interface](cli.md)
* [Examples](examples.md)

## Publications
* [Symbolic Execution and Deductive Verification Approaches to VerifyThis 2017 Challenges](https://vsl.cis.udel.edu/pubs/isola18.html), Ziqing Luo and Stephen F. Siegel, ISoLA'18.
* [Verifying Properties of Differentiable Programs](https://vsl.cis.udel.edu/pubs/math_properties_sas18.html), J. Hückelheim, Z. Luo, S.H.K. Narayanan, S.F. Siegel, and P.D. Hovland, SAS'18.
* [Towards Self-Verification in Finite Difference Code Generation](https://vsl.cis.udel.edu/pubs/correctness_2017_towards.html), J. Hückelheim, Z. Luo, F. Luporini, N. Kukreja, M. Lange, G. Gorman, S.F. Siegel, M. Dwyer, and P.D. Hovland, Correctness'17.
* [CIVL Manual](https://vsl.cis.udel.edu/lib/sw/civl/civl-manual.pdf), M.B. Dwyer, J. Edenhofner, G. Gopalakrishnan, A. Marianiello, Z. Luo, Z. Rakamaric, M. Rogers, S.F. Siegel, M. Zheng, and T.K. Zirkel
* [CIVL: The Concurrency Intermediate Verification Language](https://vsl.cis.udel.edu/pubs/civl_sc_2015.html), S.F. Siegel, M. Zheng, Z. Luo, T.K. Zirkel, A.V. Marianiello, J.G. Edenhofner, M.B. Dwyer and M.S. Rogers, SC'15.
* [Verification of MPI programs using CIVL](https://vsl.cis.udel.edu/pubs/civl_eurompi_2017.html), Z. Luo, M. Zheng, and S.F. Siegel, EuroMPI'17.
* [DOE Report of the HPC Correctness Summit](https://science.energy.gov/~/media/ascr/pdf/programdocuments/docs/2017/HPC_Correctness_Report.pdf), G. Gopalakrishnan, P.D. Hovland, C. Iancu, S. Krishnamoorthy, I. Laguna, R.A. Lethin, K. Sen, S.F. Siegel, and A. Solar-Lezama, DOE TechReport'17.
* [CIVL Solutions to VerifyThis 2016 Challenges](https://vsl.cis.udel.edu/pubs/civl_verifythis_2016.html), Stephen F. Siegel, VerifyThis'16.
* [CIVL: Applying a General Concurrency Verification Framework to C/Pthreads Programs Competition Contribution](https://vsl.cis.udel.edu/pubs/civl_svcomp_2016.html), M. Zheng, J.G. Edenhofner, Z. Luo, M.J. Gerrard, M.S. Rogers, M.B. Dwyer, and S.F. Siegel, TACAS'16
* [CIVL: Formal Verification of Parallel Programs](https://vsl.cis.udel.edu/pubs/civl_ase_2015.html), M. Zheng, M.S. Rogers, Z. Luo, M.B. Dwyer, and S.F. Siegel, ASE'15.
* [Tech Report: CIVL: The Concurrency Intermediate Verification Language](https://vsl.cis.udel.edu/pubs/civl_tr_2014.html), S.F. Siegel, M.B. Dwyer, G. Gopalakrishnan, Z. Luo, Z. Rakamaric, R. Thakur, M. Zheng, and T.K. Zirkel, Technical Report UD-CIS-2014/001.
* [VSL Publication List](https://vsl.cis.udel.edu/pubs/index.html)

## How to Cite CIVL
```bibtex
@Inproceedings{siegel-etal:2015:civl_sc,
  author = {Stephen F.\ Siegel and Manchun Zheng and Ziqing Luo and
            Timothy K.\ Zirkel and Andre V.\ Marianiello and John G.\ Edenhofner
            and Matthew B.\ Dwyer and Michael S.\ Rogers},
  title = {{CIVL}: The Concurrency Intermediate Verification Language},
  booktitle = {SC15: International Conference for High Performance
               Computing, Networking, Storage and Analysis, Proceedings},
  series = {SC '15},
  year = {2015},
  month = {Nov},
  publisher = {IEEE Press},
  address = {Piscataway, NJ, USA},
  pages = {61:1-61:12},
  doi = {10.1145/2807591.2807635}
}
```

## Bug Reports
To report a bug in CIVL, either create a [GitHub issue](https://github.com/verified-software-lab/civl/issues) or send an email to `civl-dev@googlegroups.com`.
Include as many details as possible, such as the CIVL command, source files (these may be attached), the CIVL version, and the output, including any error message.

## Developers
* Current Developers: [Stephen Siegel](https://vsl.cis.udel.edu/siegel.html) and Alex Wilton
* Previous Contributors: Matthew Dwyer, John Edenhofner, Mitchell Gerrard, Ziqing Luo, Andre Marianiello, Michael Rogers, Wenhao Wu, Yihao Yan, Manchun Zheng and Timothy Zirkel

[DeveloperPage](https://vsl.cis.udel.edu/trac/civl/wiki/DeveloperPage)

## License
CIVL is copyright from 2013 to 2026, Verified Software Laboratory, Department of Computer and Information Sciences, University of Delaware.

CIVL is distributed under the terms of the [GNU General Public License v3](https://www.gnu.org/licenses/gpl.html). (See the directory licenses in the distribution for details.)
