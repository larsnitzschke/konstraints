package tools.aqua.konstraints.smt

import tools.aqua.konstraints.theories.BoolSort

data class Interpolants(val interpolants: List<Expression<BoolSort>>) {
    override fun toString(): String = "(interpolants ${interpolants.joinToString("\n") { it.toString() }})"
}