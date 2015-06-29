
package userInterface;
import databaseManager.DatabaseManager;
import documentManager.Document;
import indexManager.StructuralSummaryManager01;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import xmlProcessor.RG.Twig;
//import xmlProcessor.RG01.Twig???
import outputGenerator.CIDStream;
import outputGenerator.DIDStream;
import outputGenerator.ExportData;
import outputGenerator.Comparator;
import outputGenerator.NodeStream;
import outputGenerator.QNameIndexSimulator;
import xmlProcessor.DBServer.DBException;
public class JFrameDB extends javax.swing.JFrame
{
    private Document doc;
    private Twig twig;
    private StructuralSummaryManager01 ssMgr;
    
    public JFrameDB()  
    {
        initComponents();
          twig=new Twig(); 
          //ssMgr=StructuralSummaryManager01.getInctance();
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jBtnImportDB = new javax.swing.JButton();
        jBtnProcess = new javax.swing.JButton();
        jTxtDbName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTxtDocument = new javax.swing.JTextArea();
        jTxtOutput = new javax.swing.JTextField();
        btnGetStream = new javax.swing.JButton();
        JBtn_NodeStream = new javax.swing.JButton();
        jBtn_Comparator = new javax.swing.JButton();
        jTxt_PlanNo = new javax.swing.JTextField();
        jBtn_ssGen = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jRBtn_RG00 = new javax.swing.JRadioButton();
        jRBtn_RG01 = new javax.swing.JRadioButton();
        jRBtn_RG05 = new javax.swing.JRadioButton();
        jRBtn_RG06 = new javax.swing.JRadioButton();
        jRBtn_RG07 = new javax.swing.JRadioButton();
        jBtn_Simulate = new javax.swing.JButton();
        jTxt_QName = new javax.swing.JTextField();
        jBtnDIDStream = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jRdBtn_DispID = new javax.swing.JRadioButton();
        jBtn_Export = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jBtnImportDB.setText("Import DB");
        jBtnImportDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnImportDBActionPerformed(evt);
            }
        });

        jBtnProcess.setText("Process Query");
        jBtnProcess.setName(""); // NOI18N
        jBtnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnProcessActionPerformed(evt);
            }
        });

        jTxtDbName.setText("m1");
        jTxtDbName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtDbNameActionPerformed(evt);
            }
        });

        jTxtDocument.setColumns(20);
        jTxtDocument.setRows(5);
        jScrollPane1.setViewportView(jTxtDocument);

        jTxtOutput.setText("here is to show output.");
        jTxtOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtOutputActionPerformed(evt);
            }
        });

        btnGetStream.setText("Get Class Id Stream");
        btnGetStream.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetStreamActionPerformed(evt);
            }
        });

        JBtn_NodeStream.setText("Get Node Stream");
        JBtn_NodeStream.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JBtn_NodeStreamActionPerformed(evt);
            }
        });

        jBtn_Comparator.setText("Compare");
        jBtn_Comparator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ComparatorActionPerformed(evt);
            }
        });

        jTxt_PlanNo.setText("30");
        jTxt_PlanNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxt_PlanNoActionPerformed(evt);
            }
        });

        jBtn_ssGen.setText("Structural Summary Mgr Generator");
        jBtn_ssGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ssGenActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRBtn_RG00);
        jRBtn_RG00.setText("RG00");
        jRBtn_RG00.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBtn_RG00ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRBtn_RG01);
        jRBtn_RG01.setText("RG01");
        jRBtn_RG01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBtn_RG01ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRBtn_RG05);
        jRBtn_RG05.setText("RG05");
        jRBtn_RG05.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBtn_RG05ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRBtn_RG06);
        jRBtn_RG06.setText("RG06");
        jRBtn_RG06.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBtn_RG06ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRBtn_RG07);
        jRBtn_RG07.setSelected(true);
        jRBtn_RG07.setText("RG07");
        jRBtn_RG07.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBtn_RG07ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBtn_RG07)
                    .addComponent(jRBtn_RG06)
                    .addComponent(jRBtn_RG05)
                    .addComponent(jRBtn_RG01)
                    .addComponent(jRBtn_RG00))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jRBtn_RG00)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBtn_RG01)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBtn_RG05)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRBtn_RG06)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, Short.MAX_VALUE)
                .addComponent(jRBtn_RG07))
        );

        jBtn_Simulate.setText("Simulate Qname Index");
        jBtn_Simulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_SimulateActionPerformed(evt);
            }
        });

        jTxt_QName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxt_QNameActionPerformed(evt);
            }
        });

        jBtnDIDStream.setText("Get DID Stream");
        jBtnDIDStream.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnDIDStreamActionPerformed(evt);
            }
        });

        jRdBtn_DispID.setText("Has DIDs?");

        jBtn_Export.setText("Export");
        jBtn_Export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBtn_Export, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jRdBtn_DispID)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtn_Export)
                    .addComponent(jRdBtn_DispID))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jTxtOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jTxtDbName))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jBtnProcess, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jBtnImportDB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtn_ssGen, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jBtnDIDStream, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnGetStream, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(JBtn_NodeStream, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(154, 154, 154)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addComponent(jTxt_PlanNo, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(31, 31, 31))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jTxt_QName, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jBtn_Simulate, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtn_Comparator, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(230, 230, 230))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBtn_Comparator, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBtnDIDStream))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBtnImportDB)
                            .addComponent(jTxtDbName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jBtnProcess)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jBtn_ssGen)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTxt_PlanNo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBtn_Simulate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxt_QName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(44, 44, 44)))
                .addComponent(btnGetStream, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBtn_NodeStream)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(jTxtOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnImportDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnImportDBActionPerformed
         DatabaseManager dbMgr=DatabaseManager.Instance;
        
             try {
                 doc=dbMgr.importDB(jTxtDbName.getText(),jTxtDocument.getText());     
                 //List <DeweyID> Deweylist=doc.getDeweyList();
                 // jTxtDocument.append(Deweylist.get(i).toString());
                     // jTxtDocument.append(Deweylist.get(i).toString());
             } catch (ParserConfigurationException | TransformerConfigurationException | IOException | DBException ex) {
                 ex.printStackTrace();
             }
             
    }//GEN-LAST:event_jBtnImportDBActionPerformed

    private void jBtnProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnProcessActionPerformed
         try {
            //how I should pass params?
           // twig.prepare(evt, params);
            if(jRBtn_RG00.isSelected())
//                 twig.customizedPrepare(101,jTxtDbName.getText(),jRBtn_RG00.getText());
                 twig.customizedPrepare(Integer.parseInt(jTxt_PlanNo.getText()),jTxtDbName.getText(),jRBtn_RG00.getText());
//                 twig.customizedPrepare(302,jTxtDbName.getText(),jRBtn_RG00.getText());
//                 twig.customizedPrepare(303,jTxtDbName.getText(),jRBtn_RG00.getText());
            else if(jRBtn_RG01.isSelected())
//                 twig.customizedPrepare(101,jTxtDbName.getText(),jRBtn_RG01.getText());
            twig.customizedPrepare(Integer.parseInt(jTxt_PlanNo.getText()),jTxtDbName.getText(),jRBtn_RG01.getText());
            else if(jRBtn_RG05.isSelected())
//                twig.customizedPrepare(101,jTxtDbName.getText(),jRBtn_RG05.getText());
                twig.customizedPrepare(Integer.parseInt(jTxt_PlanNo.getText()),jTxtDbName.getText(),jRBtn_RG05.getText());
            else if(jRBtn_RG06.isSelected())
//                twig.customizedPrepare(101,jTxtDbName.getText(),jRBtn_RG06.getText());
                twig.customizedPrepare(Integer.parseInt(jTxt_PlanNo.getText()),jTxtDbName.getText(),jRBtn_RG06.getText());
            else if(jRBtn_RG07.isSelected())
//                twig.customizedPrepare(101,jTxtDbName.getText(),jRBtn_RG07.getText());
                twig.customizedPrepare(Integer.parseInt(jTxt_PlanNo.getText()),jTxtDbName.getText(),jRBtn_RG07.getText());

            twig.execute();
        } catch (DBException ex) {
            //StackTraceElement[] DBStackTrace=ex.getStackTrace();
            ex.printStackTrace();
            System.err.println("Exception occured during execute() being run in jBtnProcessActionPerformed");
        }
    }//GEN-LAST:event_jBtnProcessActionPerformed

    private void jTxtDbNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtDbNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtDbNameActionPerformed

    private void jTxtOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtOutputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtOutputActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
         
    }//GEN-LAST:event_formWindowClosed

    private void btnGetStreamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetStreamActionPerformed
       CIDStream stream=new CIDStream();
        try {
            stream.open(jTxtDbName.getText());
            stream.close(jTxtDbName.getText());
        } catch (IOException ex) {
            System.err.println("error occured during getting stream");
        } catch (DBException ex) {
           System.err.println("DBException occured during QNameIndexSimulator()(while opening indexes)! ");
        }
    }//GEN-LAST:event_btnGetStreamActionPerformed

    private void JBtn_NodeStreamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBtn_NodeStreamActionPerformed
        NodeStream nodeS=new NodeStream();
        try {
            nodeS.open(jTxtDbName.getText());
            nodeS.close(jTxtDbName.getText());
        } catch (IOException ex) {
            System.err.println("error occured during getting stream");
            ex.printStackTrace();
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_JBtn_NodeStreamActionPerformed

    private void jBtn_ComparatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ComparatorActionPerformed
        try {
            Comparator comparator=new Comparator();
            comparator.compare();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jBtn_ComparatorActionPerformed

    private void jTxt_PlanNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxt_PlanNoActionPerformed
       
    }//GEN-LAST:event_jTxt_PlanNoActionPerformed

    private void jRBtn_RG01ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBtn_RG01ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRBtn_RG01ActionPerformed

    private void jRBtn_RG06ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBtn_RG06ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRBtn_RG06ActionPerformed

    private void jRBtn_RG05ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBtn_RG05ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRBtn_RG05ActionPerformed

    private void jRBtn_RG00ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBtn_RG00ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRBtn_RG00ActionPerformed

    private void jBtn_ssGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ssGenActionPerformed
        String xmlOutput;
        try {
            xmlOutput = new String(ssMgr.getXMLStructuralSummary(jTxtDbName.getText()));
            jTxtOutput.setText(xmlOutput);
        } catch (DBException ex) {
            System.err.println("error ocured during getXMLStructuralsummary()");
        }
         
    }//GEN-LAST:event_jBtn_ssGenActionPerformed

    private void jBtn_SimulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_SimulateActionPerformed
        try {
            QNameIndexSimulator simulator=new QNameIndexSimulator((jTxtDbName.getText()));
            jTxtOutput.setText(simulator.toString(simulator.simulate(jTxt_QName.getText())));
            simulator.close();
        } catch (IOException ex) {
            System.err.println("IOException occured during QNameIndexSimulator()(while opening indexes)! ");
        } catch (DBException ex) {
            System.err.println("DBException occured during QNameIndexSimulator()(while opening indexes)! ");
        }
    }//GEN-LAST:event_jBtn_SimulateActionPerformed

    private void jTxt_QNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxt_QNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxt_QNameActionPerformed

    private void jRBtn_RG07ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBtn_RG07ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRBtn_RG07ActionPerformed

    private void jBtn_ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ExportActionPerformed
       try
       {
               ExportData expData=new ExportData(jTxtDbName.getText());
               boolean dispDIDs=false;
               if(jRdBtn_DispID.isSelected())
                   dispDIDs=true;
               expData.exporter(dispDIDs);
               expData.close();
          
       }
       catch(IOException ex)
       {
           logManager.LogManager.log(5,"IO Error occured while Exportation"); 
       } catch (Exception ex) {
           StackTraceElement[] stackTrace = ex.getStackTrace();
           //logManager.LogManager.log(5,"Exception:"+ stackTrace[0].getFileName()+"."+stackTrace[0].getMethodName()+" at line: "+ stackTrace[0].getLineNumber());
           ex.printStackTrace();
       }
    }//GEN-LAST:event_jBtn_ExportActionPerformed

    private void jBtnDIDStreamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnDIDStreamActionPerformed
        DIDStream stream=new DIDStream();
        try {
            stream.open(jTxtDbName.getText());
            stream.close(jTxtDbName.getText());
        } catch (IOException ex) {
            System.err.println("error occured during getting stream");
        } catch (DBException ex) {
           System.err.println("DBException occured during QNameIndexSimulator()(while opening indexes)! ");
        }
    }//GEN-LAST:event_jBtnDIDStreamActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrameDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrameDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrameDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrameDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //Get the jvm heap size.
        long heapSize = Runtime.getRuntime().totalMemory();
        //Print the jvm heap size.
        System.out.println("Heap Size = " + heapSize);
        
        /* Create and display the form */
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameDB().setVisible(true);
            }
        });
        
        System.out.println("END!");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JBtn_NodeStream;
    private javax.swing.JButton btnGetStream;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JButton jBtnDIDStream;
    private javax.swing.JButton jBtnImportDB;
    private javax.swing.JButton jBtnProcess;
    private javax.swing.JButton jBtn_Comparator;
    private javax.swing.JButton jBtn_Export;
    private javax.swing.JButton jBtn_Simulate;
    private javax.swing.JButton jBtn_ssGen;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRBtn_RG00;
    private javax.swing.JRadioButton jRBtn_RG01;
    private javax.swing.JRadioButton jRBtn_RG05;
    private javax.swing.JRadioButton jRBtn_RG06;
    private javax.swing.JRadioButton jRBtn_RG07;
    private javax.swing.JRadioButton jRdBtn_DispID;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTxtDbName;
    private javax.swing.JTextArea jTxtDocument;
    private javax.swing.JTextField jTxtOutput;
    private javax.swing.JTextField jTxt_PlanNo;
    private javax.swing.JTextField jTxt_QName;
    // End of variables declaration//GEN-END:variables
}
