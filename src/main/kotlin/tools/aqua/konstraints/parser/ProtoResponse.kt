package tools.aqua.konstraints.parser

import tools.aqua.konstraints.smt.DefineFun
import tools.aqua.konstraints.smt.Expression
import tools.aqua.konstraints.smt.Model
import tools.aqua.konstraints.theories.BoolSort

internal sealed interface ProtoResponse

/** SMT "sat" response */
internal object ProtoSat : ProtoResponse

/** SMT "unsat" response */
internal object ProtoUnsat : ProtoResponse

/** SMT "unknown" response */
internal object ProtoUnknown : ProtoResponse

/** SMT model response */
internal data class ProtoModel(val defineFuns: List<ProtoDefineFun>) : ProtoResponse

/** SMT interpolants response */
internal data class ProtoInterpolants(val interpolants: List<ProtoTerm>) : ProtoResponse