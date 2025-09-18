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
import com.microsoft.z3.*
import tools.aqua.konstraints.parser.Parser
import tools.aqua.konstraints.smt.InterpolatingSMTProgram

class Testing {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Testing().run3()
    }
  }
  fun run3() {
    val program = "(set-logic LIA)\n" +
            "(declare-fun a () Int)\n" +
            "(declare-fun b () Int)\n" +
            "(declare-fun c () Int)\n" +
            "(declare-fun d () Int)\n" +
            "(declare-fun x () Int)\n" +
            "\n" +
            "\n" +
            "(assert (> x 0))\n" +
            "(assert (> x 2))\n" +
            "(assert (= a b))\n" +
            "(check-sat)\n" +
            "(get-model)\n" +
            "\n" +
            "\n" +
            // "(compute-interpolant\n" + TODO: Add support for compute-interpolant
            // "   (and (= a b) (= a c))\n" +
            // "   (and (= b d) (not (= c d))))
            ""
    val prog = InterpolatingSMTProgram(Parser.parse(program))
    print(prog.context)
    val response = "sat\n" +
            "(model \n" +
            "  (define-fun b () Int\n" +
            "    0)\n" +
            "  (define-fun a () Int\n" +
            "    0)\n" +
            "  (define-fun x () Int\n" +
            "    3)\n" +
            ")\n" +
            "unsat\n" +
            "(interpolants\n" +
            " (= c b))"
    val resp = Parser.parseResponse(response, prog.context!!)
    println(resp)
  }
  fun run2() {
    val program = Parser.parse("(set-logic LIA)\n" +
            "(declare-fun x () Int)\n" +
            "(assert (> x 0))\n" +
            "(assert (> x 0))\n" +
            "(check-sat)\n" +
            "(get-model)\n" +
            "(exit)\n"
            )
    val iProgram = InterpolatingSMTProgram(program)
    iProgram.solve()
    println("Status: ${program.status}")
    println("Model: ${program.model}")
  }
  fun run() {
    val input = "(= a b)"
    val result = Parser.term.parse(input)

    if (result.isSuccess) {
      val cmd = result.get<Any>()
      println("Parsed command: $cmd")
    } else {
      println("Parse error: ${result.message} at ${result.position}")
    }


    val file = "smt/testing.smtlib"
    val output = shellRun ("z3", listOf(file))
    val outputs = output.split("\n", limit = 2)
    if (outputs[0] == "sat") {
      println("SAT -> No Interpolant")
      return
    }
    val parse = Parser.command.parse(outputs[1].replace("\n", ""))

    println(output)
  }
}
