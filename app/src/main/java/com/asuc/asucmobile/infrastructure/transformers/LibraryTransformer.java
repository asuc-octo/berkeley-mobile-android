package com.asuc.asucmobile.infrastructure.transformers;

import com.asuc.asucmobile.infrastructure.models.Library;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LibraryTransformer {

    /**
     * Transform Firebase QuerySnapshot to domain model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Library> libraryQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.Library> libraries = new ArrayList<>();
        Library infraLibrary = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            infraLibrary = documentSnapshot.toObject(Library.class);
            if (infraLibrary != null) {
                libraries.add(libraryInfraDomainTransformer(infraLibrary));
            }

        }
        return libraries;
    }

    /**
     * Transform single library from infrastructure to domain model
     * @param library
     * @return
     */
    public com.asuc.asucmobile.domain.models.Library libraryInfraDomainTransformer(Library library) {
        return com.asuc.asucmobile.domain.models.Library.builder()
                .id(library.getId())
                .name(library.getName())
                .build();
    }

}
