/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loadsheathingpwo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author mjordan
 */
public class LoadSheathingPWO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Connection adjConn = ConnectAdjutant.connect();
        Connection hsbConn = ConnectMYSQL.connect();
        Statement stmt     = adjConn.createStatement();
        String CID         = "DEVMBSL";
        String PROJECTKEY  = "189-1151";
        String HELDFOR     = "MBSL";
        String CURRID      = "CAD";
        String SHIPVIA     = "OURTRUCK";
        String DATENOW     = "2013-10-28 00:00:00.000";
        String DATENEXT    = "2013-10-28 00:00:00.000";
        int LOCTID         = 194; //189 MBSL LIVE loctid //190 MMPL LIVE loctid //194 DEVMBSL TEST loctid //195 MMSL LIVE loctid;
        int PLANTIDH       = 194; //189 MBSL LIVE plantid //190 MMPL LIVE plantid //194 DEVMBSL TEST plantid //195 MMSL LIVE plantid;
        int OWNERID        = 50753; //50753 DEVMBSL //
        int MSNID          = 1137;
        int MSNPHASEID     = 3476;
        int CUSTID         = 50753;
        int SHIPTO         = 4108;
        int SOLDTO         = 1575;
        int BILLTO         = 1821;
        String company     = "";
        String salesOrder  = "";
        String scompany    = "";
        String saddress1   = "";
        String saddress2   = "";
        String saddress3   = "";
        String saddress4   = "";
        String saddress5   = "";
        String fob         = "";
        String taxtable    = "";
        String freight     = "";
        String terms       = "";
        double taxrate     = 0.00;
        int nextOrder      = 0;
        int sokey          = 0;
        int soKeynoh       = 0;
        
        
        
        // Get the static data setup, order number, customer data, bill to data, shipt to data,  tax rate data, and generate the order header etc...       
        String getNextOrder = (" SELECT counter.prefix, counter.number " + 
                               " FROM counter " + 
                               " WHERE counter.cid = '" + CID + "' AND " +
                               "  counter.name = 'SONO'");
        ResultSet counterRs = 
                adjConn.createStatement().executeQuery(getNextOrder);
        
        if (counterRs.next()){ 
                 nextOrder  = Integer.parseInt(counterRs.getString(2).trim())+1;
                 salesOrder = counterRs.getString(1).trim() + nextOrder;
        }  
        counterRs.close();        
        
        
        String getCustData = (" SELECT company FROM ent WHERE ent.cid = '" + 
                              CID + "' AND ent.entid = '" + SOLDTO + "'");
        ResultSet custRs = adjConn.createStatement().executeQuery(getCustData);
        if (custRs.next()){ 
                 company = custRs.getString(1).trim();                
        }  
        custRs.close();        
       
       
        String getShipData = (" SELECT company, address1, address2, address3,"+ 
                              " city, state, zipcode, country " + 
                              " from ent WHERE ent.cid = '" + CID + 
                              "' and ent.entid = '" + SHIPTO + "'");
        ResultSet shipRs = adjConn.createStatement().executeQuery(getShipData);
        if (shipRs.next()){ 
                 scompany = shipRs.getString(1).trim();
                 saddress1 = shipRs.getString(2).trim();
                 saddress2 = shipRs.getString(3).trim();
                 saddress3 = shipRs.getString(5).trim() + ", " + 
                             shipRs.getString(6).trim() + " " + 
                             shipRs.getString(7).trim();
                 saddress4 = shipRs.getString(8).trim();
                 saddress5 = shipRs.getString(4).trim();
        }  
        shipRs.close();        
       
        
        String getShiptoData = (" SELECT taxrate, shipvia, fob, taxtable, " + 
                                " freight FROM shipto WHERE custid = '" + 
                                  SHIPTO +"'");
        ResultSet shipToRs = 
                adjConn.createStatement().executeQuery(getShiptoData);
        
        if (shipToRs.next()){ 
                taxrate = Double.parseDouble(shipToRs.getString(1).trim());
                if ("".equals(shipToRs.getString(2).trim()))
                    SHIPVIA = "OURTRUCK";
                else SHIPVIA = shipToRs.getString(2).trim();
                fob = shipToRs.getString(3).trim();
                taxtable = shipToRs.getString(4).trim();
                freight = shipToRs.getString(5).trim();
        }  
        shipToRs.close();        
        
        String getBillData = ("SELECT terms FROM billto where custid= '" + 
                               SOLDTO + "'");
        ResultSet billToRs = 
                adjConn.createStatement().executeQuery(getBillData);
        
        if (billToRs.next()){ 
                 terms = billToRs.getString(1).trim();
        }  
        billToRs.close();        

       String addOrdHeader = " INSERT INTO somast (sono, custid, company," + 
                             " scompany, saddress1, saddress2, saddress3," + 
                             " saddress4, saddress5, shipid, taxrate, sp," + 
                             " shipvia, fob, freight, sodate, terms, billid," +
                             " soldid, cid, heldfor, msnid, loctid, plantidh," +
                             " currid, status, sotype)" + 
                             " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + 
                             "        ?,?,?,?,?,?,?,?)";
       PreparedStatement insertSoMast = adjConn.prepareStatement(addOrdHeader);
       
       insertSoMast.setString(1, salesOrder);
       insertSoMast.setInt(2,SOLDTO);
       insertSoMast.setString(3, company);
       insertSoMast.setString(4, scompany);
       insertSoMast.setString(5, saddress1);
       insertSoMast.setString(6, saddress2);
       insertSoMast.setString(7, saddress3);
       insertSoMast.setString(8, saddress4);
       insertSoMast.setString(9, saddress5);
       insertSoMast.setInt(10, SHIPTO);
       insertSoMast.setDouble(11, taxrate);
       insertSoMast.setString(12, "Gary Martin");
       insertSoMast.setString(13, SHIPVIA);
       insertSoMast.setString(14,fob );
       insertSoMast.setString(15, freight);
       insertSoMast.setString(16, DATENOW);
       insertSoMast.setString(17, terms);
       insertSoMast.setInt(18, SOLDTO);
       insertSoMast.setInt(19, SOLDTO);
       insertSoMast.setString(20, CID);
       insertSoMast.setString(21, HELDFOR);
       insertSoMast.setInt(22,MSNID);
       insertSoMast.setInt(23, LOCTID);
       insertSoMast.setInt(24, PLANTIDH);
       insertSoMast.setString(25,CURRID);
       insertSoMast.setString(26, "o");
       insertSoMast.setString(27, "B");
       insertSoMast.executeUpdate();
       
       ResultSet keyRs = stmt.executeQuery("SELECT @@IDENTITY FROM somast");  
               
       if(keyRs.next()) {
          soKeynoh = keyRs.getInt(1);
         //   System.out.println(soKeynoh);
       }
       keyRs.close();

       String setNextOrder = ("UPDATE counter SET counter.number = ? " + 
                             " WHERE counter.cid = '" + CID + "' AND " +
                             " counter.name = 'SONO'");
       PreparedStatement setOrderNo = adjConn.prepareStatement(setNextOrder);
       
       setOrderNo.setInt(1, nextOrder);
       setOrderNo.executeUpdate(); 
       
       // get the panels we need to create order lines for
       String getSheathPanel = " SELECT WPI.WallPanel_ID, " + 
                                "        WPI.Description, " + 
                                "        WPI.PanelLength, " + 
                                "        WPI.PanelHeight, " + 
                                "        WPI.Wieght " +
                                " FROM TS_WallPanel_Item AS WPI " +
                                " WHERE WPI.ExportKey = '" + PROJECTKEY + "' AND" + 
                                "       WPI.hasSheathing = 'y'";  
       String addWoh = " INSERT INTO woh ";
       
       
        
    }
    
}
