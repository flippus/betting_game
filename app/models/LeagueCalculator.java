/**
 *
 * Betting game realized with PlayFramework to bet different sport results with
 * other persons to determine the best better
 *
 * Copyright (C) 2014 Philipp Neugebauer, Florian Klement
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package models;

import java.util.List;

public class LeagueCalculator {

    public League league;

    public LeagueCalculator(League league) {
        this.league = league;
    }

    public int getOrderedUserPointsCount() {
        return getOrderedUserPoints().size() - 1;
    }

    public List<UserPoints> getOrderedUserPoints() {
        return UserPoints.find
                .where()
                .eq("league_id", league.id)
                .orderBy(
                        "points desc, rightResultCount desc, rightWinnerCount desc")
                        .findList();
    }

    public void calculateUserRanking() {
        List<UserPoints> userPointList = getOrderedUserPoints();
        if (userPointList.size() > 0) {
            userPointList.get(0).position = 1;
            userPointList.get(0).save();

            boolean samePosition = false;
            int position = 0;
            for (int i = 1; i < userPointList.size(); i++) {
                UserPoints userPointsI = userPointList.get(i);
                UserPoints userPointsII = userPointList.get(i - 1);
                if (userPointsI.points == userPointsII.points
                        && userPointsI.rightResultCount == userPointsII.rightResultCount
                        && userPointsI.rightWinnerCount == userPointsII.rightWinnerCount) {
                    if (samePosition) {
                        userPointList.get(i).position = position;
                    } else {
                        position = i;
                        userPointList.get(i).position = position;
                    }
                    samePosition = true;
                } else {
                    userPointList.get(i).position = i + 1;
                    samePosition = false;
                }
                userPointList.get(i).save();
            }
        }
    }

    public int getOrderedTeamsCount() {
        return getOrderedTeams().size() - 1;
    }

    public List<Team> getOrderedTeams() {
        return Team.find.where().eq("league_id", league.id)
                .orderBy("points desc, goalsDifference desc, goals desc")
                .findList();
    }

    public void calculateTeamRanking() {
        List<Team> teamPointList = getOrderedTeams();
        teamPointList.get(0).position = 1;
        teamPointList.get(0).save();

        boolean samePosition = false;
        int position = 0;
        for (int i = 1; i < teamPointList.size(); i++) {
            Team teamPointsI = teamPointList.get(i);
            Team teamPointsII = teamPointList.get(i - 1);
            if (teamPointsI.points == teamPointsII.points
                    && teamPointsI.goalsDifference == teamPointsII.goalsDifference
                    && teamPointsI.goals == teamPointsII.goals) {
                if (samePosition) {
                    teamPointList.get(i).position = position;
                } else {
                    position = i;
                    teamPointList.get(i).position = position;
                }
                samePosition = true;
            } else {
                teamPointList.get(i).position = i + 1;
                samePosition = false;
            }
            teamPointList.get(i).save();
        }
    }
}
