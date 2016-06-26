import random

def karger_run(niters):
    min_fc_edges_so_far = len(fc_edges)
    min_vertex_sets = {key: set() for key in fc_vertices.keys()}
    for iteridx in range(niters):
        if iteridx % 50 == 0: print 'Running iter: ' + str(iteridx)        
        temp_vertex_sets = {key: set() for key in fc_vertices.keys()}
        temp_fc_vertices = deepcopy(fc_vertices)
        temp_fc_edges = fc_edges[:]        
        while len(temp_fc_vertices) > k:
            # pick an edge at random and delete it
            rand_idx = int(random.random()*len(temp_fc_edges))
            random_edge = temp_fc_edges.pop(rand_idx)
            # Add v2 and temp_vertex_sets[v2] to temp_vertex_sets[v1] and delete temp_vertex_sets[v2]
            v1 = list(random_edge)[0]
            v2 = list(random_edge)[1]
            temp_vertex_sets[v1] = temp_vertex_sets[v1].union(temp_vertex_sets[v2])
            temp_vertex_sets[v1].add(v2)
            temp_vertex_sets.pop(v2, None)

            # All fc_vertices adjacent to v2 are added to temp_fc_vertices[v1] unless already present.
            # Remove v2 from temp_fc_vertices[v1].
            adj_v2 = temp_fc_vertices[v2]
            temp_fc_vertices[v1] = temp_fc_vertices[v1].union(adj_v2)
            temp_fc_vertices[v1].remove(v2)
            temp_fc_vertices.pop(v2, None)

            # Replace all instances of v2 in temp_fc_edges with v1, unless the other vertex of the edge is itself v1.
            # In the latter case, delete the edge (e.g. remove self-loops).
            # Note: Parallel fc_edges are allowed; there may be multiple instances of an edge comprised the same vertex pair.
            remove_fc_edges = []
            for i,cur_edge in enumerate(temp_fc_edges):
                if len(cur_edge) > 1:
                    cur_edge_v1 = list(cur_edge)[0]
                    cur_edge_v2 = list(cur_edge)[1]
                    if (cur_edge == random_edge):
                        remove_fc_edges.append(i)
                    elif cur_edge_v1 == v2:
                        temp_fc_edges[i] = {v1, cur_edge_v2}
                        # remove this edge from temp_fc_vertices
                        # it may have already been removed because we keep parallel fc_edges around
                        if v2 in temp_fc_vertices[cur_edge_v2]: temp_fc_vertices[cur_edge_v2].remove(v2)
                    elif cur_edge_v2 == v2:
                        temp_fc_edges[i] = {cur_edge_v1, v1}
                        # it may have already been removed because we keep parallel fc_edges around
                        if v2 in temp_fc_vertices[cur_edge_v1]: temp_fc_vertices[cur_edge_v1].remove(v2)
            # work around for delete
            temp_fc_edges = [set(i) for j, i in enumerate(temp_fc_edges) if j not in remove_fc_edges]
            #Finally: The number of final fc_edges is the number of fc_edges across the final cut in this iteration.
            #If it is less than min_fc_edges_so_far, update min_fc_edges_so_far = len(temp_fc_edges) and min_vertex_sets = temp_vertex_sets.
            if len(temp_fc_edges) < min_fc_edges_so_far:
                min_fc_edges_so_far = len(temp_fc_edges)
                min_vertex_sets = temp_vertex_sets
    return min_fc_edges_so_far, min_vertex_sets

