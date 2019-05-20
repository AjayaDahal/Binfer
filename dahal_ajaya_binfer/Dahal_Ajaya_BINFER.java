/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dahal_ajaya_binfer;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.hibernate.Criteria;

import org.json.JSONException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author ajaya
 */

public class Dahal_Ajaya_BINFER extends Application {
   Button search , viewSaved , save, clear; 
   TableView<ResultQuery> table = new TableView<ResultQuery>() ; 
   Stage  window ; 
   Stage windowSave=new Stage();
   public ObservableList<ResultQuery> data = FXCollections.observableArrayList();
  
   
   public static void main(String[] args) {
      
         launch(args); 
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage ; 
        window.setTitle ("JavaFX Dahal Ajaya (ajayadahal1000@gmail.com)") ; 
        TextField nameinput = new TextField(); 
        nameinput.setPromptText("Enter query.....");
        
        
        //Search 
        search = new Button("Search");
        Label lblHeading = new Label("Search Results");
        lblHeading.setFont(new Font("Blackadder ITC", 50));
        
        //Action Event
        search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String quer = nameinput.getText();
                String arr = null;
                try {
                    arr = wiki.query(quer);
                    //System.out.println(arr);
                } catch (JSONException ex) {
                    Logger.getLogger(Dahal_Ajaya_BINFER.class.getName()).log(Level.SEVERE, null, ex);
                }
                String arrofStr[] = arr.split("<br>", 0) ;
                System.out.println("arrofStr: "+arrofStr);
                short j = 1;
                for(int i= 1 ; i < arrofStr.length ;i+=2){
                    
                    
                    data.add( new ResultQuery(arrofStr[i-1], arrofStr[i]));
              
                    j++;
                }
            }
        });
        
        
        //View Saved
        viewSaved = new Button("View Saved");
        //Action Event
        viewSaved.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                
                 viewSave view = new viewSave();
                try {
                    view.start(windowSave);
                    view.servletData();
                    System.out.println(view.html);
                    
                } catch (Exception ex) {
                    Logger.getLogger(Dahal_Ajaya_BINFER.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
       
        });
                
        //Table Creation
        table.setEditable(true) ; 
        TableColumn idCol = new TableColumn ("ID");
        idCol.setCellValueFactory(
                new PropertyValueFactory<ResultQuery, Short>("id"));
        
         TableColumn selectCol = new TableColumn ("Select");
        selectCol.setCellValueFactory(
                new PropertyValueFactory<ResultQuery, CheckBox>("checkbox"));
        
        TableColumn titleCol = new TableColumn ("Title");
        titleCol.setCellValueFactory(
                new PropertyValueFactory<ResultQuery, String>("title"));
        
        
        TableColumn summaryCol = new TableColumn ("Summary");
        summaryCol.setCellValueFactory(
                new PropertyValueFactory<ResultQuery, String>("summary"));
        
       table.setItems(data);
        table.getColumns().addAll( selectCol, titleCol , summaryCol);
        
        
        //Save
        save = new Button("Save");
        //Action Event
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                
                System.out.println("Hibernate Configuration Executed");
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");
                configuration.addAnnotatedClass(ResultQuery.class);
                ServiceRegistry  serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
                SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                Session session = sessionFactory.openSession();
                session.beginTransaction();
                         
                Criteria c = session.createCriteria(ResultQuery.class).setProjection(Projections.max("id"));
                
                
                short id = 1;
                
                if(c.uniqueResult()!=null){
                 id = (short)c.uniqueResult();;
                }
                table.getColumns().retainAll(idCol, selectCol, titleCol, summaryCol);
                save(id);
                session.close();
            }
        });
        
        clear = new Button("clear");
        //Action Event
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
               table.getItems().clear(); 
               table.refresh();
            }
            });
        
       VBox layout = new VBox(10 ); 
       layout.setPadding(new Insets(20,20,20,20));
        
       layout.getChildren().addAll(nameinput , search ,lblHeading, viewSaved, table , save, clear);
       
       Scene scene = new Scene (layout, 1000, 1000) ;  
       window.setScene(scene); 
       window.show();
        
    
}
    public void save(short value){
        
                 
                short id = (short) (value+1);
                 for (ResultQuery dataSave: data) {
                     dataSave.setId(id);
                     if(dataSave.getCheckbox().isSelected()){
                         Configuration configuration = new Configuration();
                         configuration.configure("hibernate.cfg.xml");
                         configuration.addAnnotatedClass(ResultQuery.class);
                         ServiceRegistry  serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
                         SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                         Session session = sessionFactory.openSession();
                         session.beginTransaction();
                         session.save(dataSave);
                         session.getTransaction().commit();
                         id++;
                         session.close();
                     }   
                 
                 }
                 System.out.println("Saving Sucessful");
                 
                 
    }
    
}
