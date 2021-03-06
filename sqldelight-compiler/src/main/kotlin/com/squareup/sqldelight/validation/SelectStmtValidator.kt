/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.sqldelight.validation

import com.squareup.sqldelight.SqliteParser
import com.squareup.sqldelight.resolution.ResolutionError
import com.squareup.sqldelight.resolution.Resolver
import com.squareup.sqldelight.resolution.resolve
import com.squareup.sqldelight.types.Value

internal class SelectStmtValidator(
    private var resolver: Resolver,
    private val scopedValues: List<Value>
) {
  init {
    resolver = resolver.withScopedValues(scopedValues)
  }

  fun validate(selectStmt: SqliteParser.Select_stmtContext) : List<ResolutionError> {
    val response = arrayListOf<ResolutionError>()
    if (selectStmt.ordering_term().size > 0) {
      response.addAll(selectStmt.ordering_term().flatMap { resolver.resolve(it.expr()).errors })
    }

    if (selectStmt.K_LIMIT() != null) {
      response.addAll(selectStmt.expr().flatMap { resolver.resolve(it).errors })
    }
    return response
  }
}