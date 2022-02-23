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

package dev.nikomaru.raceassist.race.commands.place

import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.shynixn.mccoroutine.launch
import dev.nikomaru.raceassist.RaceAssist
import dev.nikomaru.raceassist.race.utils.InsideCircuit
import dev.nikomaru.raceassist.race.utils.OutsideCircuit
import dev.nikomaru.raceassist.utils.CommandUtils.canSetInsideCircuit
import dev.nikomaru.raceassist.utils.CommandUtils.canSetOutsideCircuit
import dev.nikomaru.raceassist.utils.Lang
import org.bukkit.entity.Player

@CommandMethod("ra|RaceAssist place")
class PlaceFinishCommand {
    @CommandPermission("RaceAssist.commands.place.finish")
    @CommandMethod("finish")
    fun finish(sender: Player) {
        RaceAssist.plugin.launch {
            if (canSetOutsideCircuit[sender.uniqueId] == true && canSetInsideCircuit[sender.uniqueId] == true) {
                sender.sendMessage(Lang.getComponent("now-you-not-setting-mode", sender.locale()))
                return@launch
            }
            if (canSetInsideCircuit[sender.uniqueId] == true) {
                canSetInsideCircuit.remove(sender.uniqueId)
                InsideCircuit.finish(sender)
                sender.sendMessage(Lang.getComponent("to-finish-inside-course-setting", sender.locale()))
            }
            if (canSetOutsideCircuit[sender.uniqueId] == true) {
                canSetOutsideCircuit.remove(sender.uniqueId)
                OutsideCircuit.finish(sender)
                sender.sendMessage(Lang.getComponent("to-finish-outside-course-setting", sender.locale()))
            }
        }
    }
}