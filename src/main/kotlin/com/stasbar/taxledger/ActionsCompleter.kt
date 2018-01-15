/*
 * Copyright (c) 2018 Stanislaw stasbar Baranski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *          __             __
 *    _____/ /_____ ______/ /_  ____ ______
 *   / ___/ __/ __ `/ ___/ __ \/ __ `/ ___/
 *  (__  ) /_/ /_/ (__  ) /_/ / /_/ / /
 * /____/\__/\__,_/____/_.___/\__,_/_/
 *            taxledger@stasbar.com
 */

package com.stasbar.taxledger

import com.stasbar.taxledger.options.TransactionsOptions
import com.stasbar.taxledger.translations.Text
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine


class MyCompleter : Completer {
    private val mCandidates: MutableSet<Candidate>

    init {
        mCandidates = Action.values()
                .map { getString(it.title).toLowerCase() }.toMutableSet()
                .map { Candidate(it, it, getString(Text.ACTIONS), null, null, null, true) }.toMutableSet()


        mCandidates.addAll(TransactionsOptions.arguments
                .map { Candidate(it, it, getString(Text.TRANSACTIONS_PARAMETER), null, null, null, true) })
    }

    override fun complete(reader: LineReader?, line: ParsedLine?, candidates: MutableList<Candidate>?) {
        assert(line != null)
        assert(candidates != null)

        candidates?.addAll(mCandidates)

    }

}