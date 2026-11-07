package org.ngueoko.bankaccountservice.mappers;

import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface AbtractMapper<S,D> {
    S toSouce(D destination);
    D toDestination(S source);
}
