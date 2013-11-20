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
        Connection connAdj        = ConnectAdjutant.connect();
        Connection connHsb        = ConnectMYSQL.connect();
        Statement stmt            = connAdj.createStatement();
        String testing            = "true";
        String CID                = "";
        String PROJECTKEY         = "189-1236"; //2nd Floor //1151 = 1st Floor";
        String HELDFOR            = "MBSL";
        String CURRID             = "CAD";
        String SHIPVIA            = "OURTRUCK";
        String DATENOW            = "2013-10-28 00:00:00.000";
        String DATENEXT           = "2013-10-28 00:00:00.000";
        String ADDUSER            = "MADMAX";
        String company            = "";
        String salesOrder         = "";
        String scompany           = "";
        String saddress1          = "";
        String saddress2          = "";
        String saddress3          = "";
        String saddress4          = "";
        String saddress5          = "";
        String fob                = "";
        String taxtable           = "";
        String taxable            = "";
        String freight            = "";
        String terms              = "";
        String wallMark           = "";
        String sheathItem         = "";
        String idescrip           = "";
        String sunit              = "";
        String punit              = "";
        String spriceunit         = "";
        String wallPanelDesc      = "";
        String wallPanelItem      = "";
        String socDesc            = "";
        String answerT            = "";
        String dcode              = "";
        String nextPWO            = "";
        int LOCTID                = 194; //189 MBSL LIVE loctid //190 MMPL LIVE loctid //194 DEVMBSL TEST loctid //195 MMSL LIVE loctid;
        int PLANTIDH              = 194; //189 MBSL LIVE plantid //190 MMPL LIVE plantid //194 DEVMBSL TEST plantid //195 MMSL LIVE plantid;
        int OWNERID               = 50753; //50753 DEVMBSL //
        int MSNID                 = 1137;
        int MSNPHASEID            = 3476;
        int CUSTID                = 50753;
        int SHIPTO                = 4108;
        int SOLDTO                = 1575;
        int BILLTO                = 1821;
        int nextOrder             = 0;
        int sokey                 = 0;
        int soKeynoh              = 0;
        int wohKeynoh             = 0; 
        int wallPanelId           = 0;
        int sheathIkey            = 0;
        int decMultiplier         = 1000; //3 decimals
        int line                  = 0;
        int quantity              = 1;
        int sotranKeyno           = 0;
        int socKeyno              = 0;
        int question              = 0;
        int socQuestKeyno         = 0;
        double taxrate            = 0.00;
        double wallPanelLengthMm      =  0.00;
        double wallPanelHeightMm      =  0.00;
        double wallPanelGrossAreaSqm  = 0.00;
        double wallPanelNetAreaSqm    = 0.00;
        double wallPanelLengthFt      = 0.00;
        double wallPanelHeightFt      = 0.00;
        double wallPanelGrossAreaSqft = 0.00;
        double wallPanelNetAreaSqft   = 0.00;
        double wallPanelWeight        = 0.00;
        double sheathAvgCost          = 0.00;
        double cost                   = 0.00;
        double price                  = 0.00;
        double sellfact               = 0.00;
        double iweight                = 0.00;
        double spricefact             = 0.00;
        double priceFact              = 0.00;
        double scost                  = 0.00;
        double sprice                 = 0.00;
        double extstot                = 0.00;
        double exttax                 = 0.00;  
        double exttot                 = 0.00;
        double extcost                = 0.00;
        double answer                 = 0.00;
                  
        if ("true".equals(testing)) 
            CID = "DEVMBSL";
        else CID = "MBSL";
        
        
        // Get the static data setup, order number, customer data, bill to data, shipt to data,  tax rate data, and generate the order header etc...       
        String getNextOrder = (" SELECT counter.prefix, counter.number " + 
                               " FROM counter " + 
                               " WHERE counter.cid = '" + CID + "' AND " +
                               "  counter.name = 'SONO'");
        ResultSet counterRs = 
                connAdj.createStatement().executeQuery(getNextOrder);
        
        if (counterRs.next()){ 
                 nextOrder  = Integer.parseInt(counterRs.getString(2).trim())+1;
                 salesOrder = counterRs.getString(1).trim() + nextOrder;
        }  
        counterRs.close();        
        
        
        String getCustData = (" SELECT company FROM ent WHERE ent.cid = '" + 
                              CID + "' AND ent.entid = '" + SOLDTO + "'");
        ResultSet custRs = connAdj.createStatement().executeQuery(getCustData);
        if (custRs.next()){ 
                 company = custRs.getString(1).trim();                
        }  
        custRs.close();        
       
       
        String getShipData = (" SELECT company, address1, address2, address3,"+ 
                              " city, state, zipcode, country " + 
                              " from ent WHERE ent.cid = '" + CID + 
                              "' and ent.entid = '" + SHIPTO + "'");
        ResultSet shipRs = connAdj.createStatement().executeQuery(getShipData);
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
                connAdj.createStatement().executeQuery(getShiptoData);
        
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
                connAdj.createStatement().executeQuery(getBillData);
        
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
       PreparedStatement insertSoMast = connAdj.prepareStatement(addOrdHeader);
       
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
       PreparedStatement setOrderNo = connAdj.prepareStatement(setNextOrder);
       
       setOrderNo.setInt(1, nextOrder);
       setOrderNo.executeUpdate(); 
       
       nextPWO = PROJECTKEY + "-001";
         
         String getNextPWO = ("(SELECT TOP 1 wono " + 
                              " FROM woh " + 
                              " WHERE wono LIKE('" + PROJECTKEY +"%')" + 
                              " ORDER BY wono DESC");
         ResultSet rsGetNextPWO = connAdj.createStatement().executeQuery(getNextPWO);
         
         if (rsGetNextPWO.next()){
             String[] pwoSegments = rsGetNextPWO.getString(1).split("-");
             int pwoLastSegment = Integer.valueOf(pwoSegments[4]);
                 pwoLastSegment = pwoLastSegment++;
              
                  if(pwoLastSegment <  10)  
                       nextPWO = PROJECTKEY + "00" + String.valueOf(pwoLastSegment);
                  else if(pwoLastSegment >= 10 && pwoLastSegment <= 99) 
                       nextPWO = PROJECTKEY + "0" + String.valueOf(pwoLastSegment);
                  else nextPWO = PROJECTKEY + String.valueOf(pwoLastSegment);              
         }rsGetNextPWO.close();

         String addWoh = (" INSERT INTO woh (custid, duedate, sokey," +
                         "                  adduser, adddate, ownerid, heldfor, " +
                         "                  loctid, wono, cid, startdate, dcode, " + 
                         "                  plantid, OLDPLANTID, OLDLOCTID," + 
                         "                  OLDOWNERID, OLDHELDFOR)" +
                         "   VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
         PreparedStatement insertWoh = connAdj.prepareStatement(addWoh);
         insertWoh.setInt(1,CUSTID);
         insertWoh.setString(2, DATENEXT);
         insertWoh.setInt(3, soKeynoh);
         insertWoh.setString(4, ADDUSER);
         insertWoh.setString(5, DATENOW);
         insertWoh.setInt(6, OWNERID);
         insertWoh.setString(7,HELDFOR);
         insertWoh.setInt(8, LOCTID);
         insertWoh.setString(9, nextPWO);
         insertWoh.setString(10, CID);
         insertWoh.setString(11, DATENOW);
         insertWoh.setString(12, dcode);
         insertWoh.setInt(13, LOCTID);
         insertWoh.setInt(14,LOCTID);
         insertWoh.setInt(15, LOCTID);
         insertWoh.setInt(16, OWNERID);
         insertWoh.setString(17,HELDFOR);

         keyRs = stmt.executeQuery("SELECT @@IDENTITY FROM woh");  
               
       if(keyRs.next()) {
          wohKeynoh = keyRs.getInt(1);
         //   System.out.println(soKeynoh);
       }keyRs.close();

         
       String getWallPanel = (" SELECT IM.ikey, IM.descrip, IM.item from itemmaster AS IM" + 
                              " INNER JOIN pcxref AS IA ON IM.ikey = IA.parentid " +
                              " WHERE IM.cid      = '" + CID + "'" +
                              "       IA.type     = 'PA' AND " +
                              "       IA.cidkeyno = IM.cid");
       
       ResultSet rsGetWallPanel = connAdj.createStatement().executeQuery(getWallPanel);
           if (rsGetWallPanel.next()){
               wallPanelId   = rsGetWallPanel.getInt(1);
               wallPanelDesc = rsGetWallPanel.getString(2);
               wallPanelItem = rsGetWallPanel.getString(3);
           }rsGetWallPanel.close();  
       
       // get the panels we need to create order lines for
       String getSheathPanel =  " SELECT WPI.WallPanel_ID, " + 
                                "        WPI.Description, " + 
                                "        WPI.PanelLength, " +
                                "        CONVERT_MM_DEC4_FEET(WPI.PanelLength)," +
                                "        WPI.PanelHeight, " + 
                                "        CONVERT_MM_DEC4_FEET(WPI.PanelHeight)," +  
                                "        WPI.AreaGross,   " +
                                "        CONVERT_mAREA_2SQFT(WPI.AreaGross),   " +
                                "        WPI.AreaNet,     " + 
                                "        CONVERT_mAREA_2SQFT(WPI.AreaNet),     " +
                                "        WPI.Wieght,       " +
                                "        (SELECT INV.Inventory Code FROM TS_Sheathing_Item AS SHI " +
                                "         FROM ts_inv_master AS INV " +  
                                "         INNER JOIN TS_Sheathing_Item AS SHI ON INV.ImperialName = SHI.Material " + 
                                "         WHERE SHI.WallPanel_ID = WPI.WallPanel_ID LIMIT 1 ) "  +
                                " FROM TS_WallPanel_Item AS WPI " +
                                " WHERE WPI.ExportKey = '" + PROJECTKEY + "' AND" + 
                                "       WPI.hasSheathing = 1 ";  
       ResultSet rsGetSheathPanel = connHsb.createStatement().executeQuery(getSheathPanel);
           while (rsGetSheathPanel.next()){
               wallPanelId            = rsGetSheathPanel.getInt(1);
               wallMark               = rsGetSheathPanel.getString(2);
               wallPanelLengthMm      = rsGetSheathPanel.getDouble(3);
               wallPanelLengthFt      = rsGetSheathPanel.getDouble(4);
               wallPanelHeightMm      = rsGetSheathPanel.getDouble(5);
               wallPanelHeightFt      = rsGetSheathPanel.getDouble(6);
               wallPanelGrossAreaSqm  = rsGetSheathPanel.getDouble(7);
               wallPanelGrossAreaSqft = rsGetSheathPanel.getDouble(8);
               wallPanelNetAreaSqm    = rsGetSheathPanel.getDouble(9);
               wallPanelNetAreaSqft   = rsGetSheathPanel.getDouble(10);
               wallPanelWeight        = rsGetSheathPanel.getDouble(11);
               sheathItem             = rsGetSheathPanel.getString(12);
               String getItemKey = ("SELECT ikey, descrip, cost, price, sellfact," +
                                    " taxable, unit_sell, unit_pur, unitw," +
                                    " spriceunit, spricefact " +
                                   " FROM itemmaster WHERE cid = '" + CID +
                                   "' AND  item = '"+ sheathItem + "'");
                  ResultSet rsGetItemKey = connAdj.createStatement().executeQuery(getItemKey);
                  if (rsGetItemKey.next()){
                       sheathIkey = rsGetItemKey.getInt(1);
                       sheathAvgCost = rsGetItemKey.getDouble(2);                         
                       idescrip = rsGetItemKey.getString(2).trim();
                       cost     = rsGetItemKey.getDouble(3);
                       price    = rsGetItemKey.getDouble(4);
                       sellfact = rsGetItemKey.getDouble(5);
                       taxable  = rsGetItemKey.getString(6);
                       sunit    = rsGetItemKey.getString(7);
                       punit    = rsGetItemKey.getString(8);
                       iweight  = rsGetItemKey.getDouble(9);
                       spriceunit = rsGetItemKey.getString(10);
                       spricefact = rsGetItemKey.getDouble(11);
                  
                       if ("MSFT".equals(punit) || "MF".equals(punit))
                           priceFact = 0.001;  
                           scost    = (double)Math.round((priceFact * cost) * 100) / 100;
                           sprice   = (double)Math.round((priceFact * price) * 100) / 100;  
                           extstot  = ((double)Math.round((sprice * quantity) * 100 ) / 
                                        100);
                           exttax   = (double)Math.round(((sprice * quantity) * 
                                      (taxrate /100)) * 100 ) / 100;
                           exttot   = (((double)Math.round((sprice * quantity) * 100 ) 
                                        / 100) + ((double)Math.round((sprice * quantity) *
                                        (taxrate / 100 ) * 100) / 100)); 
                       }rsGetItemKey.close();
              
                       socDesc = ("Wall Mark: "   + wallMark + " " + 
                                  "PanelLength: " + Double.toString(wallPanelLengthFt) + " " +
                                  "PanelHeight: " + Double.toString(wallPanelHeightFt) + " " +
                                  "Gross SqFt: "  + Double.toString(wallPanelGrossAreaSqft) + " " +
                                  "Net SqFt: "    + Double.toString(wallPanelNetAreaSqft));        
                       
                       String getDept = (" SELECT XR.type " +
                                         " FROM pcxref as XR" +
                                         " INNER JOIN rd AS RD on RD.text1 = XR.type" +
                                         " INNER JOIN rh AS RH on RH.keyno = RD.rhkeyno" +
                                         " WHERE XR.parentid = '" + wallPanelId + 
                                         "' AND RH.cid = '" + CID + "' AND RD.text2='Dept'");
                       ResultSet rsGetDept = connAdj.createStatement().executeQuery(getDept);
                       if (rsGetDept.next()){
                           dcode = rsGetDept.getString(1);
                       }rsGetDept.close();
                       
              String addsoLine = ("INSERT INTO sotran (sono, custid, linenum," + 
                                " item, qtyord, cost, price, " + 
                                " expdate, shipdate, ikey, descrip, loctid," + 
                                " estdate, keynoh, " + 
                                " reqdate, taxable, unit, unitfact, heldfor," + 
                                " adduser, adddate, " + 
                                " edituser, editdate, pgroup, qty2bill," + 
                                " actqty, shiptoid, phaseid, " + 
                                " projid, ownerid, extstot, exttot, exttax," +
                                " socdesc, taxrated, sprice, " + 
                                " spriceunit, taxtable, olistprice, qtyopen," +
                                " plantid, getprice, unitw2, " + 
                                " spricefact, bextcost, bexttax, bexttot," + 
                                " bextstot, bprice, bsprice, " + 
                                " bunitprice, bstdcost2, bgetprice, " + 
                                " BLISTPRICE, AMSPARTNO, needprod, resetprod) " + 
                                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + 
                                       " ?,?,?,?,?,?,?,?,?,?,?,?,?,?," + 
                                       " ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + 
                                       " ?,?,?,?,?,?,?,?,?)");
              PreparedStatement addLine = connAdj.prepareStatement(addsoLine);
            
            addLine.setString(1, salesOrder);
            addLine.setInt(2, SOLDTO);
            addLine.setInt(3,line);
            addLine.setString(4, wallPanelItem);
            addLine.setInt(5, quantity);
            addLine.setDouble(6, cost);
            addLine.setDouble(7, (double)Math.round(sprice * decMultiplier) / 
                                  decMultiplier );
            addLine.setString(8, "n");
            addLine.setString(9, DATENEXT);
            addLine.setInt(10, wallPanelId);
            addLine.setString(11, idescrip);
            addLine.setInt(12, LOCTID);
            addLine.setString(13, DATENEXT);
            addLine.setInt(14, soKeynoh);
            addLine.setString(15, DATENEXT);
            addLine.setString(16, taxable);
            addLine.setString(17, sunit);
            addLine.setDouble(18, sellfact);
            addLine.setString(19, HELDFOR);
            addLine.setString(20, "MADMAX");
            addLine.setString(21, DATENOW);
            addLine.setString(22, " ");
            addLine.setString(23, null);
            addLine.setString(24, "M");
            addLine.setInt(25, quantity);
            addLine.setInt(26, quantity);
            addLine.setInt(27,SHIPTO);
            addLine.setInt(28, MSNPHASEID);
            addLine.setInt(29, MSNID);
            addLine.setInt(30, OWNERID);
            addLine.setDouble(31, (double)Math.round(extstot * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(32, (double)Math.round(exttot * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(33, (double)Math.round(exttax * decMultiplier) / 
                                   decMultiplier );
            addLine.setString(34, socDesc);
            addLine.setDouble(35, taxrate);
            addLine.setDouble(36, (double)Math.round(price * decMultiplier) / 
                                   decMultiplier );
            addLine.setString(37, spriceunit);
            addLine.setString(38, taxtable);
            addLine.setDouble(39, (double)Math.round(price * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(40, quantity);
            addLine.setInt(41, LOCTID);
            addLine.setDouble(42, (double)Math.round(price  * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(43, iweight);
            addLine.setDouble(44, spricefact);
            addLine.setDouble(45, (double)Math.round(extcost * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(46, (double)Math.round(exttax * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(47, (double)Math.round(exttot * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(48, (double)Math.round(extstot * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(49, (double)Math.round(sprice * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(50, (double)Math.round(price * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(51, (double)Math.round(sprice * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(52, (double)Math.round(extcost * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(53, (double)Math.round(price * decMultiplier) / 
                                   decMultiplier );
            addLine.setDouble(54, (double)Math.round(price * decMultiplier) / 
                                   decMultiplier );
            addLine.setString(55,"");
            addLine.setString(56,"y");
            addLine.setString(57,"y");
            addLine.executeUpdate();
            
            keyRs = stmt.executeQuery("SELECT @@IDENTITY FROM sotran"); 
              if(keyRs.next()) {
                sotranKeyno = keyRs.getInt(1);
               //  System.out.println(sotranKeyno);
              }keyRs.close();
           
             String getSOCitemID = ("SELECT keyno FROM socitem WHERE cid = '" + 
                                  CID + "' AND ikey = " + wallPanelId);
            ResultSet socRs = 
                    connAdj.createStatement().executeQuery(getSOCitemID);
               
          if(socRs.next()) {
            socKeyno = socRs.getInt(1);
              System.out.println(socKeyno);
          }socRs.close();
          
         for (question = 0; question < 6; question++) {
              String getSOCquest = ("SELECT keyno from socquestion" + 
                                    " where keynoh = " + socKeyno + 
                                    "AND questno = " + question);
              ResultSet getQuest = 
                      connAdj.createStatement().executeQuery(getSOCquest);    
     
              if (getQuest.next()) {
                 socQuestKeyno = getQuest.getInt(1);
              }
             
              switch (question) {
                  case 1: // System.out.println("Piece Mark5: " + pieceMark);
                           answerT = wallMark;
                          // System.out.println("Piece Mark6: " + pieceMark);
                          break;
                  case 2: answer = wallPanelHeightFt;
                          break;
                  case 3: answer = wallPanelLengthFt;
                          break;
                  case 4: answer = wallPanelGrossAreaSqft;
                          break;
                  case 5: answer = wallPanelNetAreaSqft;
                          break;
              }           
              
              String addSOtranans = ("INSERT INTO sotranans " + 
                                     " (keynod, question, answer, adduser," + 
                                     " adddate) VALUES (?,?,?,?,?)");
              PreparedStatement addAnswers = 
                      connAdj.prepareStatement(addSOtranans);
              
              addAnswers.setInt(1, sotranKeyno);
              addAnswers.setInt(2, socQuestKeyno);
              if (question == 1)
              addAnswers.setString(3, answerT);
              else
              addAnswers.setDouble(3, answer);   
              addAnswers.setString(4, "MADMAX");
              addAnswers.setString(5, DATENOW);
              addAnswers.executeUpdate();
          } 
          
          }rsGetSheathPanel.close();
    }
    
}
