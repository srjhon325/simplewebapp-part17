package com.devglan.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import controlador.Post;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.springframework.http.RequestEntity.options;

@Component
public class SocketHandler extends TextWebSocketHandler {

    String planta;

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        //the messages will be broadcasted to all users.
        sessions.add(session);

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);
        if (value.get("name").equalsIgnoreCase("")) {

            // Get a reference to our posts
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("post");

            // Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataReferencia) {

                    for (DataSnapshot hijoData : dataReferencia.getChildren()) {

                        Post objPost = new Post();
                        objPost = hijoData.getValue(Post.class);

                        System.out.println("******************************************" + hijoData.getKey());
                        for (WebSocketSession webSocketSession : sessions) {
                            try {
                                webSocketSession.sendMessage(new TextMessage("{'nodo':'','sl':" + objPost.getSl() + ",'sw':" + objPost.getSw() + ",'pl':" + objPost.getPl() + ",'pw':" + objPost.getPw() + "}"));
                                planta = hijoData.getKey();
                                Thread.sleep(2000);

                            } catch (IOException ex) {
                                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

        } else {

            for (WebSocketSession webSocketSession : sessions) {

                Thread.sleep(1000);

                webSocketSession.sendMessage(new TextMessage("Hello clasificacion " + value.get("name") + " !"));

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("post");

                Map<String, Object> userUpdates = new HashMap<>();
                userUpdates.put(planta + "/nodo", value.get("name").replaceAll("\"", ""));

                usersRef.updateChildrenAsync(userUpdates);

                Thread.sleep(500);

            }

        }

        /*for (WebSocketSession webSocketSession : sessions) {

            Map<String, String> value2 = new Gson().fromJson(message.getPayload(), Map.class);
            webSocketSession.sendMessage(new TextMessage("Hello clasificacion " + value2.get("name") + " !"));

        }*/
        //session.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
    }

}
