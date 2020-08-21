.data
blank : .asciiz " "
_1str : .asciiz "final b:\n"
.text
__init:
	lui $sp, 0x8000
	addi $sp, $sp, 0x0000
	move $fp, $sp
	add $gp, $gp, 0x8000
	jal main
	li $v0, 10
	syscall
Mars_PrintInt:
	li $v0, 1
	syscall
	li $v0, 4
	move $v1, $a0
	la $a0, blank
	syscall
	move $a0, $v1
	jr $ra
Mars_GetInt:
	li $v0, 5
	syscall
	jr $ra
Mars_PrintStr:
	li $v0, 4
	syscall
	jr $ra
func:
	subu $sp, $sp, 32
	move $8, $4
	move $2, $8
	move $sp, $fp
	jr $31
main:
	subu $sp, $sp, 32
	li $25, 0
	move $9, $25
	li $25, 1
	move $10, $25
	li $25, 2
	move $11, $25
	mul $25, $10, $11
	move $12, $25
	sub $25, $9, $12
	move $11, $25
	xori $12, $9, 0xffffffff
	move $11, $12
	subu $sp, $sp, 4
	sw $fp, ($sp)
	move $fp, $sp
	sw $31, 20($sp)
	move $4, $9
	jal func
	lw $31, 20($sp)
	lw $fp, ($sp)
	addu $sp, $sp, 4
	move $12, $2
	li $25, 1
	add $25, $12, $25
	move $11, $25
	sgt $12, $9, $10
	beq $12, $0, L1
	li $25, 1
	move $9, $25
	j L2
L1:
	li $25, 1
	move $10, $25
L2:
	li $25, 0
	move $9, $25
L3:
	li $25, 10
	slt $13, $9, $25
	beq $13, $0, L4
	li $25, 2
	add $25, $10, $25
	move $10, $25
	li $25, 5
	sgt $14, $10, $25
	beq $14, $0, L5
	j L4
L5:
	li $25, 1
	add $25, $9, $25
	move $9, $25
	j L3
L4:
	la $15, _1str
	subu $sp, $sp, 4
	sw $fp, ($sp)
	move $fp, $sp
	sw $31, 20($sp)
	move $4, $15
	jal Mars_PrintStr
	lw $31, 20($sp)
	lw $fp, ($sp)
	addu $sp, $sp, 4
	move $24, $2
	subu $sp, $sp, 4
	sw $fp, ($sp)
	move $fp, $sp
	sw $31, 20($sp)
	move $4, $10
	jal Mars_PrintInt
	lw $31, 20($sp)
	lw $fp, ($sp)
	addu $sp, $sp, 4
	move $25, $2
	li $0, 0
	move $2, $0
	move $sp, $fp
	jr $31
