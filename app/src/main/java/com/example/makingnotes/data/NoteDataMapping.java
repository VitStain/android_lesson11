package com.example.makingnotes.data;

import java.security.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class NoteDataMapping {

    public static class Fields {
        public final static String DATE = "date";
        public final static String TITLE = "title";
        public final static String CONTENT = "content";
    }

    public static Notes toNoteData(String id, Map<String, Object> doc) {
//        Timestamp timeStamp = (Timestamp)doc.get(Fields.DATE);
        Notes answer = new Notes((String) doc.get(Fields.TITLE),
                (String) doc.get(Fields.CONTENT),
                (String) doc.get(Fields.DATE));
//                timeStamp.toDate());
        answer.setId(id);
        return answer;
    }

    public static Map<String, Object> toDocument(Notes note) {
        Map<String, Object> answer = new HashMap<>();
        answer.put(Fields.TITLE, note.getTitle());
        answer.put(Fields.CONTENT, note.getContent());
        answer.put(Fields.DATE, note.getDate());
        return answer;
    }


}
