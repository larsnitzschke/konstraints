/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Konstraints Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.aqua.konstraints.solvers.z3

import com.lordcodes.turtle.shellRun
import java.nio.file.Path
import kotlin.io.path.pathString
import tools.aqua.konstraints.parser.Parser
import tools.aqua.konstraints.smt.Interpolants
import tools.aqua.konstraints.smt.Model
import tools.aqua.konstraints.smt.SMTProgram
import tools.aqua.konstraints.smt.SatStatus
import tools.aqua.konstraints.solvers.Solver

/** A solver that calls Z3 as an external process using SMT-LIB2 format. */
class ShellZ3Solver : Solver {
  private var tempFiles = mutableListOf<Path>()
  private var status: SatStatus = SatStatus.PENDING
  private var model: Model? = null
  private var interpolant: Interpolants? = null

  /**
   * Solves the given SMT program by writing it to a temporary file and invoking the Z3 binary.
   * Parses the output to determine satisfiability, model, and interpolants if available.
   *
   * @param program The SMT program to solve.
   * @return The satisfiability status of the program.
   */
  override fun solve(program: SMTProgram): SatStatus {
    // Write program to a temp file
    tempFiles.add(kotlin.io.path.createTempFile(suffix = ".smt2"))
    tempFiles.last().toFile().writeText(program.commands.joinToString("\n") { it.toString() })

    // Call the z3 binary with the file as input
    val output = shellRun("z3", listOf(tempFiles.last().pathString))

    // Parse the output
    val parsed = Parser.parseResponse(output, program.context!!)
    status = parsed.lastOrNull { it is SatStatus } as? SatStatus ?: SatStatus.UNKNOWN
    model = parsed.lastOrNull { it is Model } as? Model
    interpolant = parsed.lastOrNull { it is Interpolants } as? Interpolants

    return status
  }

  override fun getModelOrNull(): Model? {
    return model
  }

  override fun getModel(): Model {
    requireNotNull(model)
    return model!!
  }

  override fun isModelAvailable(): Boolean {
    return model != null
  }

  fun getInterpolantOrNull(): Interpolants? {
    return interpolant
  }

  fun getInterpolant(): Interpolants {
    requireNotNull(interpolant)
    return interpolant!!
  }

  fun isInterpolantAvailable(): Boolean {
    return interpolant != null
  }

  override fun close() {
    tempFiles.forEach { it.toFile().deleteOnExit() }
  }
}
