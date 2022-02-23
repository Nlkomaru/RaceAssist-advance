/*
 * Copyright © 2021-2022 Nikomaru <nikomaru@nikomaru.dev>
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nikomaru.raceassist.race.commands.race

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.shynixn.mccoroutine.launch
import dev.nikomaru.raceassist.RaceAssist
import dev.nikomaru.raceassist.utils.CommandUtils
import dev.nikomaru.raceassist.utils.CommandUtils.displayLap
import dev.nikomaru.raceassist.utils.CommandUtils.getCentralPoint
import dev.nikomaru.raceassist.utils.CommandUtils.getCircuitExist
import dev.nikomaru.raceassist.utils.CommandUtils.getGoalDegree
import dev.nikomaru.raceassist.utils.CommandUtils.getLapCount
import dev.nikomaru.raceassist.utils.CommandUtils.getPolygon
import dev.nikomaru.raceassist.utils.CommandUtils.getRaceDegree
import dev.nikomaru.raceassist.utils.CommandUtils.getReverse
import dev.nikomaru.raceassist.utils.CommandUtils.judgeLap
import dev.nikomaru.raceassist.utils.CommandUtils.stop
import dev.nikomaru.raceassist.utils.Lang
import dev.nikomaru.raceassist.utils.coroutines.minecraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.ScoreboardManager
import java.awt.Polygon
import kotlin.math.hypot
import kotlin.math.roundToInt

@CommandMethod("ra|RaceAssist race")
class RaceDebugCommand {

    @CommandPermission("RaceAssist.commands.race.debug")
    @CommandMethod("debug <raceId>")
    fun debug(sender: Player, @Argument(value = "raceId", suggestions = "raceId") raceId: String) {
        RaceAssist.plugin.launch {
            if (CommandUtils.returnRaceSetting(raceId, sender)) return@launch
            if (!getCircuitExist(raceId, true) || !getCircuitExist(raceId, false)) {
                sender.sendMessage(Lang.getComponent("no-exist-race", sender.locale()))
                return@launch
            }

            val insidePolygon = getPolygon(raceId, true)
            val outsidePolygon = getPolygon(raceId, false)
            if (insidePolygon.npoints < 3 || outsidePolygon.npoints < 3) {
                sender.sendMessage(Lang.getComponent("no-exist-race", sender.locale()))
                return@launch
            }
            val reverse = getReverse(raceId) ?: false
            val lap: Int = getLapCount(raceId)
            val threshold = 40
            val centralXPoint: Int =
                getCentralPoint(raceId, true) ?: return@launch sender.sendMessage(Lang.getComponent("no-exist-central-point", sender.locale()))
            val centralYPoint: Int =
                getCentralPoint(raceId, false) ?: return@launch sender.sendMessage(Lang.getComponent("no-exist-central-point", sender.locale()))
            val goalDegree: Int =
                getGoalDegree(raceId) ?: return@launch sender.sendMessage(Lang.getComponent("no-exist-goal-degree", sender.locale()))
            var beforeDegree = 0
            var currentLap = 0
            var counter = 0
            var passBorders = 0
            var totalDegree = 0
            val lengthCircle = calcurateLength(insidePolygon)




            for (timer in 0..4) {
                val showTimer = async(minecraft) {
                    sender.showTitle(Title.title(Lang.getComponent("${5 - timer}", sender.locale()), Lang.getComponent("", sender.locale())))
                }
                delay(1000)
                showTimer.await()
            }

            sender.showTitle(Title.title(Lang.getComponent("to-notice-start-message", sender.locale()), Lang.getComponent(" ", sender.locale())))

            while (counter < 180 && stop[raceId] != true) {

                val nowX = sender.location.blockX
                val nowY = sender.location.blockZ
                val relativeNowX = if (!reverse) nowX - centralXPoint else -1 * (nowX - centralXPoint)
                val relativeNowY = nowY - centralYPoint
                val currentDegree = getRaceDegree(relativeNowY.toDouble(), relativeNowX.toDouble())

                val beforeLap = currentLap
                val calculateLap = async(Dispatchers.Default) {
                    currentLap += judgeLap(goalDegree, beforeDegree, currentDegree, threshold)
                    passBorders += judgeLap(0, beforeDegree, currentDegree, threshold)
                    displayLap(currentLap, beforeLap, sender, lap)
                    beforeDegree = currentDegree
                    totalDegree = currentDegree + (passBorders * 360)
                }

                if (insidePolygon.contains(nowX, nowY) || !outsidePolygon.contains(nowX, nowY)) {
                    sender.sendActionBar(Lang.getComponent("outside-the-racetrack", sender.locale()))
                }

                calculateLap.await()

                val manager: ScoreboardManager = Bukkit.getScoreboardManager()
                val scoreboard = manager.newScoreboard
                val objective: Objective = scoreboard.registerNewObjective(Lang.getText("scoreboard-ranking", sender.locale()),
                    "dummy",
                    Lang.getComponent("scoreboard-context", sender.locale()))

                objective.displaySlot = DisplaySlot.SIDEBAR

                val score = objective.getScore("raceId = $raceId    goalDegree = $goalDegree°")
                score.score = 7
                val data1 = objective.getScore("relativeNowX = $relativeNowX m relativeNowY = $relativeNowY m")
                data1.score = 6
                val data2 = objective.getScore("passBorders = $passBorders times currentLap = $currentLap times")
                data2.score = 5
                val data3 = objective.getScore("totalDegree = $totalDegree° currentDegree = $currentDegree°")
                data3.score = 4
                val data4 =
                    objective.getScore("lengthCircle = ${lengthCircle.roundToInt()} m nowLength = ${(lengthCircle / 360 * totalDegree).roundToInt()} m")
                data4.score = 3
                val degree = Lang.getComponent("scoreboard-now-lap-and-now-degree", sender.locale(), currentLap.toString(), totalDegree.toString())
                val displayDegree = objective.getScore(LegacyComponentSerializer.legacySection().serialize(degree))
                displayDegree.score = 2
                val residue = objective.getScore(Lang.getText("time-remaining", sender.locale(), (180 - counter).toString()))
                residue.score = 1
                sender.scoreboard = scoreboard
                counter++
                delay(1000)
            }
            delay(2000)

            sender.scoreboard.clearSlot(DisplaySlot.SIDEBAR)

        }
    }

    private fun calcurateLength(insidePolygon: Polygon): Double {
        var total = 0.0
        val insideX = insidePolygon.xpoints
        val insideY = insidePolygon.ypoints
        for (i in 0 until insidePolygon.npoints) {
            total += if (i <= insidePolygon.npoints - 2) {
                hypot((insideX[i] - insideX[i + 1]).toDouble(), (insideY[i] - insideY[i + 1]).toDouble())
            } else {
                hypot((insideX[i] - insideX[0]).toDouble(), (insideY[i] - insideY[0]).toDouble())
            }
        }
        return total
    }

}