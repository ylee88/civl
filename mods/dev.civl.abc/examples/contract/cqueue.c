#include <civlc.cvh>
#include <seq.cvh>
#pragma CIVL ACSL

$scope root = $here;

typedef struct cqueue_t{
  int data[];
  $proc owner;
} cqueue;

/*@ depends_on \nothing;  
  @ assigns \nothing;
  @ reads \nothing; 
  @*/
$atomic_f void create(cqueue* q)
{
  q = (cqueue*)$malloc(root, sizeof(cqueue));
  q->owner=$proc_null;
  $seq_init(&q->data, 0, NULL);
}
/*@ depends_on \read(&(q->owner));
  @ assigns q->owner;
*/
$atomic_f _Bool lock(cqueue* q)
{
  if(q->owner==$self)
    return $true;
  if(q->owner == $proc_null){
    q->owner=$self;
    return $true;
  }else
    return $false;
}
/*@ depends_on \call(enqueue, q, ...), \call(dequeue,q, ...);
  @ reads q->data;
  @ assigns \nothing;
  @*/
$atomic_f int size(cqueue* q)
{
  return $seq_length(&q->data); 
}
/*@ reads q->owner;
  @ behavior success:
  @   assumes q->owner==$self;
  @   depends_on \read(&(q->owner)) + \write(&(q->owner));
  @   assigns q->owner;
  @ behavior failure:
  @   assumes q->owner!=$self;
  @   depends_on \nothing;
  @   assigns \nothing;
  @*/
$atomic_f _Bool unlock(cqueue* q)
{
  if(q->owner==$self)
  {
    q->owner=$proc_null;
    return $true;
  }
  return $false;
}
/*@ depends_on \call(enqueue,q, ...);
  @ reads q->data;
  @*/
$atomic_f _Bool enqueue(cqueue* q, int v)
{
  $seq_append(&q->data, &v, 1);
}
/*@ depends_on \call(enqueue, q, ...);
  @ reads q->data;
  @*/
$atomic_f _Bool dequeue(cqueue* q, int* res)
{
  $seq_remove(&q->data, $seq_length(&q->data)-1, res, 1);
}
