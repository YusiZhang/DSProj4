DSProj4
=======

CMU 15640 MPI Cluster

Complie java files using sh build.sh at root dir.

Run Sequential DNA program:
sh run_dna_seq.sh
Run MPI-based DNA program:
sh run_dna_paral.sh
to run with different processes, you need to modify the run_dna_paral.sh file:
mpirun -np process_Number java MainDNA cluster_Number
Run Sequential 2D points program:
sh run_point_seq.sh
Run MPI-based 2D points program:
sh run_point_paral.sh
to run with different processes, you need to modify the run_dna_paral.sh file:
mpirun -np process_Number java MainDNA cluster_Number

