from itertools import izip
from tabulate import tabulate

def sum_term_frequencies(db_cursor, nterms, nclusters, super_nodes, min_vertex_sets):
    overall_frequencies = [0]*nterms
    cluster_frequencies = [[0]*nterms]*nclusters

    for cidx, supernode in enumerate(super_nodes):
        vertices_in_cluster = min_vertex_sets[supernode]
        all_nodes = list(vertices_in_cluster)
        all_nodes.append(supernode)
        for docid in all_nodes:
            cur.execute("SELECT TfIdfVector FROM processed_documents WHERE Id = " + str(docid))
            result = cur.fetchone()[0]
            tf_idf_vector = [float(x) for x in result]
            cluster_frequencies[cidx] = map(sum, izip(cluster_frequencies[cidx], tf_idf_vector))
            overall_frequencies = map(sum, izip(overall_frequencies, tf_idf_vector))
    return cluster_frequencies, overall_frequencies

def normalize_term_frequencies(nterms, cluster_frequencies, overall_frequencies):
    cluster_freqs_normalized = []
    for cidx in range(nclusters):
        curr_cluster = cluster_frequencies[cidx]
        cluster_freqs_normalized.append(
            [curr_cluster[i]/overall_frequencies[i] if overall_frequencies[i] > 0 else 0 for i in range(nterms)]
        )
    return cluster_freqs_normalized

def print_clusters(cur, super_nodes, cluster_freqs_normalized, num_terms, terms):
    clusters = [[supernode] + list(min_vertex_sets[supernode]) for supernode in super_nodes]

    for cidx, cluster in enumerate(clusters):
        print "\_\_\_\_\n"
        print "**Cluster " + str(cidx+1) + "**\n"
        curr_cluster = cluster_freqs_normalized[cidx]
        degrees = [len(vertices[vertex]) for vertex in cluster]
        centroid_id = cluster[degrees.index(max(degrees))]
        cur.execute("SELECT Original FROM processed_documents WHERE Id = " + str(centroid_id))
        centroid = cur.fetchone()[0]
        doc = centroid.replace("_", " ").replace("( ", " (")
        print '*Representative:*'
        print '> ' + doc + "\n"
        # workaround for reversing the array
        sorted_frequency_idcs = np.argsort(curr_cluster)[::-1]
        rel_frequencies = [curr_cluster[sorted_frequency_idcs[i]] for i in range(num_terms)]
        top_terms = [terms[sorted_frequency_idcs[i]] for i in range(num_terms)]
        print tabulate(zip(top_terms, rel_frequencies), headers=['Top Terms', 'Relative Frequency'], tablefmt="pipe")
        print "\n\n"

def build_distinct_graphs(vertices):
    graphs = []
    unvisited = set(vertices.keys())

    #Detect distinct graphs
    while len(unvisited) > 0:
        # for every vertex, find all of its connected components and recurse on those vertices
        visited = []
        current_vertex = unvisited.pop()
        visited.append(current_vertex)
        stack_to_visit = list(vertices[current_vertex])
        while len(stack_to_visit) > 0:
            current_vertex = stack_to_visit.pop()
            current_adj_vtcs = vertices[current_vertex]
            if current_vertex not in visited: visited.append(current_vertex)
            if current_vertex in unvisited: unvisited.remove(current_vertex)        
            for v in current_adj_vtcs:
                if v not in visited:
                    stack_to_visit.insert(0, v)
        graphs.append(visited)
    return graphs

def create_graph(alignments):
    edges = [tuple([x[0],x[1]]) for x in alignments]
    vertices = {}
    for edge in edges:
        v1 = edge[0]
        v2 = edge[1]
        if v1 in vertices.keys():
            vertices[v1].add(v2)
        else:
            vertices[v1] = {v2}
        if v2 in vertices.keys():
            vertices[v2].add(v1)
        else:
            vertices[v2] = {v1}
    return [edges, vertices]
  
