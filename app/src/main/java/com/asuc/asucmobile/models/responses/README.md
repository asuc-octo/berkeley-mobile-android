# Responses Doc 1.0

## Summary

Contain POJO responses from the backend, used in conjunction with Retrofit. Gets the responses as 
the greatest ancestor in response JSON, and parses into POJO with Retrofit. Varies for each call, in
accordance to backend schema.

## Cafes

Note: Cafes has a deeper JSON structure, so we have to make two different responses. `CafeMenuResponse` and 
`CafesResponse`. We then do some data preprocessing in `Cafe`.