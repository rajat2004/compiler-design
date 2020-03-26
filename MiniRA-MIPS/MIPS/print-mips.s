	.text
main:
	sw $ra, 0($sp)        # store main return address
	addi $sp, $sp, -4     # move stack pointer downwards
	addi $a0, $0, 10      # pass function argument
	jal foo               # call foo
	lw $ra, 4($sp)        # restore main return address
	addi $sp, $sp, 4      # move stack pointers upwards
	addi $v0, $0, 0       # set return value of main as 0
	jr $ra                # return
foo:
	addi $t0, $0, 1       # save iterated value (i) in t0
	addi $t1, $a0, 0      # save number of values to print (j) in t1
	sw $ra, 0($sp)        # save return address in stack
	addi $sp, $sp, -4     # move stack downwards
	j foo_loop_cond       # go check the loop condition
foo_loop:
	addi $a0, $t0, 0      # set i as argument of print_int
	sw $t0, 0($sp)        # store t0 in stack
	sw $t1, -4($sp)       # store t1 in stack
	addi $sp, $sp, -8     # move stack pointer downwards
	addi $v0, $0, 1       # set print_int syscall code
	syscall               # call print_int to print number
	addi $a0, $0, 32      # pass space code as argument of print_char
	addi $v0, $0, 11      # set print_char syscall code
	syscall               # call print_char to print space
	lw $t0, 8($sp)        # restore t0 from stack
	lw $t1, 4($sp)        # restore t1 from stack
	addi $sp, $sp, 8      # move stack upwards
	addi $t0, $t0, 1      # increment i
foo_loop_cond:
	slt $t2, $t1, $t0     # set t2 if j < i (not i <= j)
        beq $t2, $0, foo_loop # loop back to start if not set
	addi $a0, $0, 10      # pass new line code as argument of print_char
	addi $v0, $0, 11      # set print_char syscall code
	syscall               # call print_chat to print new line
	lw $ra, 4($sp)        # restore return address from stack
	addi $sp, $sp, 4      # move stack upwards
	jr $ra                # return
