package baseball;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {
    private final int numOfTeams;
    private final Map<String, Team> nameToTeam;
    private final String[] indexToName;
    private final static double INFINITY = Double.POSITIVE_INFINITY;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        validateNull(filename);
        In in = new In(filename);
        numOfTeams = in.readInt();
        nameToTeam = new HashMap<>((int) (numOfTeams / 0.75 + 1));
        indexToName = new String[numOfTeams];
        for (int i = 0; i < numOfTeams; i++) {
            Team cur = new Team(in.readString(), in.readInt(), in.readInt(), in.readInt(), i);
            int[] competeWith = new int[numOfTeams];
            for (int j = 0; j < numOfTeams; j++) {
                competeWith[j] = in.readInt();
            }
            cur.competeWith = competeWith;
            nameToTeam.put(cur.name, cur);
            indexToName[i] = cur.name;
        }
        for (int i = 0; i < numOfTeams; i++) {
            eliminate(i);
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return nameToTeam.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        validateNull(team);
        validateTeam(team);
        return nameToTeam.get(team).win;
    }

    // number of losses for given team
    public int losses(String team) {
        validateNull(team);
        validateTeam(team);
        return nameToTeam.get(team).loss;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validateNull(team);
        validateTeam(team);
        return nameToTeam.get(team).remaining;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateNull(team1, team2);
        validateTeam(team1, team2);
        int other = nameToTeam.get(team2).index;
        return nameToTeam.get(team1).competeWith[other];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validateNull(team);
        validateTeam(team);
        return nameToTeam.get(team).eliminated;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team)) {
            return null;
        }
        return nameToTeam.get(team).certificate;
    }

    private void eliminate(int index) {
        if (trivialElimination(index)) {
            return;
        }
        nontrivialElimination(index);
    }

    private double teamToTarget(Team eliminated, int curIndex) {
        Team cur = nameToTeam.get(indexToName[curIndex]);
        return eliminated.win + eliminated.remaining - cur.win;
    }

    private double sourceToCompete(int either, int other) {
        return against(indexToName[either], indexToName[other]);
    }

    private boolean trivialElimination(int index) {
        Team self = nameToTeam.get(indexToName[index]);
        for (int i = 0; i < numOfTeams; i++) {
            if (index == i) {
                continue;
            }
            if (self.win + self.remaining < nameToTeam.get(indexToName[i]).win) {
                self.eliminated = true;
                self.certificate = new ArrayList<>();
                self.certificate.add(indexToName[i]);
                return true;
            }
        }
        return false;
    }

    private void nontrivialElimination(int index) {
        Team self = nameToTeam.get(indexToName[index]);

        int numOfTeams = this.numOfTeams - 1;
        int numOfCompete = (numOfTeams * (numOfTeams - 1)) >> 1;
        int total = 2 + numOfCompete + numOfTeams;
        int source = 0, target = total - 1;

        FlowNetwork flowNetwork = new FlowNetwork(total);
        int[] teamIndexToVerIndex = new int[this.numOfTeams];

        // i and j refers to vertex index
        int teamIndex = 0;
        for (int i = numOfCompete + 1; i < total - 1; i++) {
            if (teamIndex == index) {
                teamIndex++;
            }
            // connect team vertex to target
            double capacity = teamToTarget(self, teamIndex);
            flowNetwork.addEdge(new FlowEdge(i, target, capacity));

            teamIndexToVerIndex[teamIndex] = i;
        }
        // i and j refers to team index
        int vertexIndex = 1;
        for (int i = 0; i < this.numOfTeams; i++) {
            if (i == index) {
                continue;
            }
            for (int j = i + 1; j < this.numOfTeams; j++) {
                if (j == index) {
                    continue;
                }
                // connect source to compete vertex
                double capacity = sourceToCompete(i, j);
                flowNetwork.addEdge(new FlowEdge(source, vertexIndex, capacity));
                // connect compete vertex to team vertex
                int verEither = teamIndexToVerIndex[i], verOther = teamIndexToVerIndex[j];
                flowNetwork.addEdge(new FlowEdge(vertexIndex, verEither, INFINITY));
                flowNetwork.addEdge(new FlowEdge(vertexIndex, verOther, INFINITY));
            }
        }

        FordFulkerson maxFlow = new FordFulkerson(flowNetwork, source, target);
        for (FlowEdge edge : flowNetwork.adj(source)) {
            if (source == edge.from() && edge.flow() != edge.capacity()) {
                self.eliminated = true;
                self.certificate = new ArrayList<>();
                for (int i = 0; i < this.numOfTeams; i++) {
                    if (i == index) {
                        continue;
                    }
                    if (maxFlow.inCut(teamIndexToVerIndex[i])) {
                        self.certificate.add(indexToName[i]);
                    }
                }
                break;
            }
        }
    }


    private void validateNull(Object...o) {
        for (Object ind : o) {
            if (ind == null) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void validateTeam(String...teams) {
        for (String team : teams) {
            if (!nameToTeam.containsKey(team)) {
                throw new IllegalArgumentException();
            }
        }
    }

    private class Team {
        private final int index;
        private final int win, loss, remaining;
        private final String name;
        private int[] competeWith;
        private boolean eliminated;
        private List<String> certificate;

        public Team(String name, int win, int loss, int remaining, int index) {
            this.index = index;
            this.win = win;
            this.loss = loss;
            this.remaining = remaining;
            this.name = name;
            eliminated = false;
        }
    }
}
