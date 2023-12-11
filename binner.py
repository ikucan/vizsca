@nb.jit(nopython=True)
def numba_binner(t:np.array, val:np.array, bin_start:np.array, bin_end:np.array, ):
    '''
    a simple binner. expects bin start and bin end timestamps to be pre-generated
    @param t: timestamp array for each value
    @param val: values to be binned
    @param bin_start: array of bin start times for each value
    @param bin_end: array of bin end times for each value

    '''
    ## figure out some metrics about the data being binned
    n_ticks, n_bins = len(t), len(np.unique(bin_start))
    bin_size = bin_end[0] - bin_start[0]

    #
    # create target arrays
    #
    bin_start_index = np.full(n_bins, t[0])
    bin_end_index = np.full(n_bins, t[0])
    n_samples = np.full(n_bins, 0)
    o = np.full(n_bins, 0.0)
    h = np.full(n_bins, 0.0)
    l = np.full(n_bins, 0.0)
    c = np.full(n_bins, 0.0)
    twap = np.full(n_bins, 0.0)
    t0 = np.full(n_bins, t[0])
    t1 = np.full(n_bins, t[0])

    # do the binning. j is leading index, i is traling index, bi is the bin index
    i, j, bi = 0, 0, 0
    while j < n_ticks:
        while (j < n_ticks) & (bin_start[i] == bin_start[j]):
            j += 1

        # j has overshot the bin, so the bin index is on the prev bin
        bin_start_index[bi] = bin_start[j - 1]
        bin_end_index[bi]   = bin_end[j - 1]

        n_samples[bi] = j - i

        times_bin = t[i:j]
        ticks_bin = val[i:j]

        o[bi] = ticks_bin[0]
        h[bi] = np.max(ticks_bin)
        l[bi] = np.min(ticks_bin)
        c[bi] = ticks_bin[-1]
        t0[bi] = times_bin[0]
        t1[bi] = times_bin[-1]

        # #####
        # for some calcs, forward fill from prev bin and to end of bin
        # #####
        if bi > 0:
            #  if there is a prev bin and if the previous bin exactly preceding in time
            # forward fill the last tick to the bin boundary
            if bin_start_index[bi] - bin_start_index[bi - 1] == bin_size:
                times_bin = insert(times_bin, 0, bin_start_index[bi])
                ticks_bin = insert(ticks_bin, 0, val[i-1])

        # forward fill last tick to bin boundary
        times_bin = insert(times_bin, -1, bin_end_index[bi])

        dt = np.diff(times_bin)
        twap[bi] = np.sum(dt * ticks_bin)/np.sum(dt)

        bi += 1
        i = j
            
    return bin_start_index, bin_end_index, t0, t1, n_samples, o, h, l, c, twap
