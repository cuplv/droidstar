<style>
.okin {
    background-color: LightGreen;
}

.okout {
    background-color: LightBlue;
}

.errin {
    background-color: LightPink;
}
</style>

# Demo Tutorial

Testing the tutorial page.

## Following the learning process

DroidStar learns by performing **queries**, which are sequences of
callins interspersed with pauses to listen for callbacks.

These pauses are written in the following logs as <span
class="okin">(CB?)</span> blocks.  Each pause gets a blue response
block after the ">>", which either contains a callback or a <span
class="okout">(none)</span> if none were seen.

Queries are **accepted** if running the callins in sequence does not
throw an error.  In this case they will appear green and followed by
(blue) callbacks.  If one of the callins throws an error, the query is
**rejected** and appears red in the log.

## Understanding the results

The result of a learning session is a **callback typestate**,
presented below as a graph, which describes a class's stateful behavior.

A class object begins in state 0, and allows only those callins which
have arrows leaving state 0 to be called.  Upon having an accepted
callin invoked, the object changes to the state indicated by the
callin's <span class="okin">green</span> arrow.

Somes states have a callback enabled, marked by a <span
class="okout">blue</span> arrow.  This indicates that if you leave the
object in this state, you will observe that callback.

Callback typestates are built using query results.  Try looking back
through the query log that produced this result, and comparing queries
to the graph.  For <span class="okin">accepted</span> queries, you
will be able to create a path from the callins and callbacks in the
graph (where <span class="okout">(none)</span> makes no move).  For
<span class="errin">rejected</span> queries, you will find that one
callin in the sequence is applied in a state where it is not enabled
(there is no arrow labeled with it leaving the state).
