/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package dahal_ajaya_binfer;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author ajaya
 */
public class viewSave {
    
    TableView<ResultQuery> table = new TableView<ResultQuery>() ;
    Stage  window ;
    Button delete;
    public ObservableList<ResultQuery> data=FXCollections.observableArrayList();
    public List <Short>dataID;
    public List <String>dataTitle;
    public List <String>dataSummary;
    public String html;
    public void start(Stage secondaryStage) throws Exception{
        window = secondaryStage ;
        window.setTitle ("View Saved (ajayadahal1000@gmail.com)") ;
        //Table Creation
        table.setEditable(true) ;
        TableColumn idCol = new TableColumn ("ID");
        idCol.setCellValueFactory(
                new PropertyValueFactory<ResultQuery, CheckBox>("id"));
        TableColumn selectCol = new TableColumn ("select");
        selectCol.setCellValueFactory(
                new PropertyValueFactory<ResultQuery, CheckBox>("checkbox"));
        TableColumn titleCol = new TableColumn ("Title");
        titleCol.setCellValueFactory(
                new PropertyValueFactory<ResultQuery, String>("title"));
        TableColumn summaryCol = new TableColumn ("Summary");
        summaryCol.setCellValueFactory(
                new PropertyValueFactory<ResultQuery, String>("summary"));
        
        //pull data from database to table
        
        addData();
        table.setItems(data);
        
        table.getColumns().addAll( selectCol, titleCol , summaryCol);
        
        //Delete button
        delete = new Button("Delete");
        //Action Event
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Session session;
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");
                configuration.addAnnotatedClass(ResultQuery.class);
                ServiceRegistry  serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
                SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                session = sessionFactory.openSession();
                
                
                // session.beginTransaction();
                for (ResultQuery dataDelete: data) {
                    if(dataDelete.getCheckbox().isSelected()){
                        for (short id = 0;id<100;id++){
                            //dataDelete.setId(id);
                            try{
                                session.beginTransaction();
                                session.delete(dataDelete);
                                System.out.println("Deleted");
                                session.getTransaction().commit();
                            }
                            catch(NullPointerException ex){
                                System.out.println("Exception While deleting: "+ex);
                                if (ex!=null){
                                    session.beginTransaction().rollback();
                                }
                            }
                        }
                    }
                }
                data.clear();
                addData();
                table.setItems(data);
                table.refresh();
                session.close();
                
                
            }
        });
        
        VBox layout = new VBox(10 );
        layout.setPadding(new Insets(20,20,20,20));
        layout.getChildren().addAll( table, delete);
        Scene scene = new Scene (layout, 500, 750) ;
        window.setScene(scene);
        window.show();
        
    }
    public void addData(){
        Session session;
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(ResultQuery.class);
        ServiceRegistry  serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        session = sessionFactory.openSession();
        session.beginTransaction();
        
        Criteria criteria = session.createCriteria(ResultQuery.class);
        List list = criteria.list();
        
        
        //for Servlet
        StringBuilder buf = new StringBuilder();
        buf.append("<html>" +
           "<body>" +
           "<table>" +
           "<tr>" +
           "<th>ID</th>" +
           "<th>Title</th>" +
           "<th>Summary</th>" +
           "</tr>");
        
        short id = 0;
        while(id<=list.size()+100){
            try{
                ResultQuery objectUser = (ResultQuery) session.get(ResultQuery.class,id);
                data.add( new ResultQuery(id, objectUser.getTitle(), objectUser.getSummary()));
                buf.append("<tr><td>")
       .append(id)
       .append("</td><td>")
       .append(objectUser.getTitle())
       .append("</td><td>")
       .append(objectUser.getSummary())
       .append("</td></tr>");
                
            }catch(NullPointerException ex){
                System.out.println("Ex: "+ex);
                
            }
            id++;
        }
        buf.append("</table>" +
           "</body>" +
           "</html>");
        this.html = buf.toString();
        System.out.println("id: "+dataID);
        session.getTransaction().commit();
        
        session.close();
        
        
    }
    public String servletData(){
        System.out.println("ServletData Executed!");
        return html;
}
    
}

