import numpy as np
from scipy.stats import multivariate_normal

#X = np.random.rand(100,2)
X = np.array(fomc_docs, dtype = 'float64')
k = 10
pis = np.repeat(1.0/k, k)

# pick random observations as starting points
N = X.shape[0]
M = X.shape[1]
# fixme these are found quantities
# pcs = np.percentile(a = X, q = [10,50,60,90], axis = 0)
pcs = X[np.random.choice(N,k),:]
starting_cov = np.diagflat([0.18]*M)

pdfs = []
probabilities = []
for kidx in range(k):
    mu = pcs[kidx,:]
    distribution = multivariate_normal(mean=mu, cov=starting_cov)
    probabilities.append(distribution.pdf(X))
    pdfs.append(distribution)

probabilities = np.array(probabilities)
curr_loglikelihood = np.log(np.sum([pis[kidx]*probabilities[kidx,:] for kidx in range(k)]))
curr_loglikelihood
max_iters = 100
assignments = {kidx: set() for kidx in range(k)}
#for iter_idx in range(max_iters):

gamma_matrix = np.zeros((N, k), dtype = 'float64')
z_matrix = np.zeros((N, k))

for nidx in range(N):
    conditional_probabilities = np.repeat(0.0, k)
    x = X[nidx,:]
    for kidx in range(k):
        # what is the probibility of x for distribution k?
        prob = pdfs[kidx].pdf(x)
        posterior_prob = pis[kidx]*prob
        conditional_probabilities[kidx] = posterior_prob
    conditional_probabilities_vector = conditional_probabilities / np.sum(conditional_probabilities)
    if np.any(np.isnan(conditional_probabilities_vector)):
      print 'here'
      conditional_probabilities_vector = [0.]*k
    gamma_matrix[nidx,:] = conditional_probabilities_vector
    assignment_vector = np.zeros(k)
    assignment_ki = np.argmax(conditional_probabilities)
    assignments[assignment_ki] = assignments[assignment_ki].union([nidx])
    assignment_vector[np.argmax(conditional_probabilities)] = 1
    z_matrix[nidx,:] = assignment_vector

Nk = np.sum(z_matrix, axis = 0)
Nk

pis = Nk/N
    # new mus
mus = []
covs = []
pdfs = []
probabilities = []
for kidx in range(k):
    x_idcs = list(assignments[kidx])
    k_x = X[x_idcs,:]
    mu = np.mean(k_x, axis = 0)
    if k_x.shape[0] == 1:
        cov = np.cov(X[x_idcs*2,:].T)
    else:
        cov = np.cov(k_x.T)
    distribution = multivariate_normal(mean=mu, cov=cov)
    probabilities.append(distribution.pdf(X))
    pdfs.append(distribution)

  
probabilities = np.array(probabilities)
curr_loglikelihood = np.log(np.sum([pis[kidx]*probabilities[kidx,:] for kidx in range(k)]))
curr_loglikelihood

