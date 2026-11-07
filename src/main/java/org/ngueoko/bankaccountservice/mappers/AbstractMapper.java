package org.ngueoko.bankaccountservice.mappers;


public interface AbstractMapper<S,D> {
    S toSouce(D destination);
    D toDestination(S source);
}
