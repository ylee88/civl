! @expect error

program hello
  implicit none
  !$CVL $assert(.FALSE.)
end
