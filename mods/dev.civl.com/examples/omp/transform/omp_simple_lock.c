/* 
 * Modified from OpenMP API Examples Ver.4.5.0 (Nov. 2016): 
 * Sec. 6.9.4 Simple Lock Routines Pg. 204
 */ 

#include <civlc.cvh>
#include <stdio.h>
#include <omp.h>

void skip(int i) {
  printf("[TID: %d]: Failed to test the lock.\n", i);
}

void work(int i) {
  printf("[TID: %d]: Succed to test the lock.\n", i); 
}

int main() {
  omp_lock_t lck;
  int id, data = -1;
  omp_init_lock(&lck);

  #pragma omp parallel shared(lck, data) private(id)
  {
    id = data;
    id = omp_get_thread_num();
    
    omp_set_lock(&lck);
#ifdef BAD
    data = 100;
#endif
    /* only one thread at a time can execute this printf */
    printf("My thread id is %d.\n", id);
    omp_unset_lock(&lck);
    
    while (! omp_test_lock(&lck)) {
      skip(id); /* we do not yet have the lock, 
                   so we must do something else */
    }
    
    work(id);   /* we now have the lock
                   and can do the work */
    
    omp_unset_lock(&lck);
  }
  omp_destroy_lock(&lck);

  return 0;
}
