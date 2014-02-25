package org.mary.apps.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.GroupLayout;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.compiere.apps.ADialog;
import org.compiere.apps.AppsAction;
import org.compiere.apps.ConfirmPanel;
import org.compiere.apps.StatusBar;
import org.compiere.apps.form.FormFrame;
import org.compiere.apps.form.FormPanel;
import org.compiere.grid.ed.VDate;
import org.compiere.grid.ed.VLookup;
import org.compiere.minigrid.MiniTable;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTabbedPane;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;

public class VPromptPaymentDiscount extends PromptPaymentDiscount implements FormPanel,ActionListener,ChangeListener{

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if (e.getSource().equals(m_ConfirmPanel.getOKButton()))
			generateDocs();
		else if (e.getSource().equals(m_ConfirmPanel.getCancelButton()))
			dispose();
		else if (e.getSource().equals(m_refreshButton))
			refresh();
		else if (e.getSource().equals(m_selectAllButton))
			selectAll();
		else if (e.getSource().equals(m_Type))
		{
			KeyNamePair type =(KeyNamePair) m_Type.getSelectedItem();
			
			if (type.getKey()==1)
			{
				removeRows((DefaultTableModel)m_Table.getModel());
				setTableModel(true);
				
			}
			else
			{
				removeRows((DefaultTableModel)m_Table.getModel());
				setTableModel(false);
			}
		}
		
		if (m_C_DocType!=null)
		{
			if (m_C_DocType.getValue()!=null)
			{
				Env.setContext(ctx, m_WindowNo, "C_DocTypeTarget_ID", Integer.parseInt(m_C_DocType.getValue().toString()));
				int AD_Column_ID = 3845;        //  C_InvoiceLine.C_Charge_ID
				MLookup lookup=null;
				try {
					lookup = MLookupFactory.get(ctx, m_WindowNo, AD_Column_ID, DisplayType.TableDir, Env.getLanguage(ctx), "C_Charge_ID", 0, false, " (C_Charge.C_Charge_ID IN ( " +
							"SELECT c.C_Charge_ID  " + 
							"FROM C_Charge c " +
							"JOIN C_ChargeType ct ON (ct.C_ChargeType_ID = c.C_ChargeType_ID) " +
							"JOIN C_ChargeType_Doctype ctd ON (ctd.C_ChargeType_ID = ct.C_ChargeType_ID) " +
							"JOIN  C_DocType dt ON (dt.C_DocType_ID =ctd.C_DocType_ID) " +
							"WHERE  ctd.C_DocType_ID =  " + m_C_DocType.getValue().toString() +
							") OR "+
							"(SELECT COUNT(*) FROM C_ChargeType_DocType WHERE AD_Client_ID=" + Env.getAD_Client_ID(ctx)+") = 0)");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				m_C_Charge = new VLookup ("C_Charge_ID", false, false, true, lookup);
				m_C_Charge.setMandatory(true);
			}
		}
				
	}

	@Override
	public void init(int WindowNo, FormFrame frame) {
		// TODO Auto-generated method stub
		try
		{
			m_WindowNo = WindowNo;
			m_frame = frame;
			jbInit();
			dynInit();
			
			frame.getContentPane().add(m_tabbedPane, BorderLayout.CENTER);
			frame.getContentPane().add(m_ButtomPane, BorderLayout.SOUTH);
			//frame.getContentPane().add(m_statusBar, BorderLayout.SOUTH);

			m_frame.pack();
			
			
			
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}
	
	/**
	 * Creacion de objetos de Ventana
	 * @author carlos Parada
	 * @date 07/09/2012
	 * @throws Exception
	 */
	private void jbInit() throws Exception
	{
		Env.setContext(ctx, m_WindowNo, "AD_Org_ID", Env.getAD_Org_ID(ctx));
		Env.setContext(ctx, m_WindowNo, "AD_Client_ID", Env.getAD_Org_ID(ctx));
		Env.setContext(ctx, m_WindowNo, "IsSOTrx", "Y");
		
		m_lblC_BPartner = new CLabel(Msg.translate(ctx, "C_BPartner_ID"));
		m_lblProcessDate = new CLabel(Msg.translate(ctx, "XXPaymentDate"));
		m_lblC_DocType = new CLabel(Msg.translate(ctx, "C_DocType_ID"));
		m_lblType = new CLabel(Msg.translate(ctx, "TrxType"));
		
		int AD_Column_ID = 3499;        //  C_Invoice.C_BPartner_ID
		MLookup lookup = MLookupFactory.get (ctx, m_WindowNo,0, AD_Column_ID, DisplayType.TableDir);
		m_C_BPartner = new VLookup ("C_BPartner_ID", false, false, true, lookup);
		m_C_BPartner.setMandatory(true);
		
		AD_Column_ID = 3781;        //  C_Invoice.C_DocTypeTarget_ID
		lookup = MLookupFactory.get (ctx, m_WindowNo,0, AD_Column_ID, DisplayType.TableDir);
		m_C_DocType = new VLookup ("C_DocTypeTarget_ID", false, false, true, lookup);
		m_C_DocType.setMandatory(true);
		m_C_DocType.addActionListener(this);
		
		AD_Column_ID = 3845;        //  C_InvoiceLine.C_Charge_ID
		//lookup = MLookupFactory.get (ctx, m_WindowNo,0, AD_Column_ID, DisplayType.TableDir);
		lookup = MLookupFactory.get(ctx, m_WindowNo, AD_Column_ID, DisplayType.TableDir, Env.getLanguage(ctx), "C_Charge_ID", 0, false, " C_Charge.C_Charge_ID = 1000567 ");
		m_C_Charge = new VLookup ("C_Charge_ID", false, false, true, lookup);
		m_C_Charge.setMandatory(true);
		
		
		m_ProcessDate = new VDate(DisplayType.Date);
		m_ProcessDate.setMandatory(true);
		m_ProcessDate.setValue(Env.getContext(ctx, "#Date"));
		m_Table = new MiniTable();
		m_ConfirmPanel.setCancelVisible(true);
		
		//Agregando Boton de Seleccion de Items
		AppsAction selectAllAction = new AppsAction ("SelectAll", KeyStroke.getKeyStroke(KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK), null);
    	m_selectAllButton = (CButton)selectAllAction.getButton();
    	m_selectAllButton.setMargin(new Insets (0, 10, 0, 10));
    	m_selectAllButton.setDefaultCapable(true);
    	m_selectAllButton.addActionListener(this);
    	m_ConfirmPanel.addButton(m_selectAllButton);
		
    	//Agregando Botom de Refrescar en el panel
		m_refreshButton = ConfirmPanel.createRefreshButton(false);
		m_refreshButton.setMargin(new Insets (1, 10, 0, 10));
		m_refreshButton.setDefaultCapable(true);
		m_refreshButton.addActionListener(this);
		m_ConfirmPanel.addButton(m_refreshButton);
		
		//Agregando Escucha a Botones Aceptar y Cancelar
		m_ConfirmPanel.getOKButton().addActionListener(this);
		m_ConfirmPanel.getCancelButton().addActionListener(this);
		
		//Boton Por defect Refrescar
		m_frame.getRootPane().setDefaultButton(m_refreshButton);
		
		m_Table.setMultiSelection(true);
		m_Table.setRowSelectionAllowed(true);
		setTableModel(true);
			//
		m_Table.autoSize();
		//m_Table.getModel().addTableModelListener(this);
		//	Info
		m_statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "InvoiceDiscountGenerateSel"));//@@
		m_statusBar.setStatusDB(" ");
		m_tabbedPane.addChangeListener(this);
		
		Vector <KeyNamePair> np = new Vector<KeyNamePair>();
		np.add(new KeyNamePair(1, Msg.translate(ctx, "Discount")));
		np.add(new KeyNamePair(2, Msg.translate(ctx, "Interes")));
		
		m_Type = new CComboBox(np);
		
		m_Type.addActionListener(this);
		
		//Panel Result
		message = new JEditorPane()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -2271852928089812014L;

			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				Dimension m = getMaximumSize();
				if ( d.height > m.height || d.width > m.width ) {
					Dimension d1 = new Dimension();
					d1.height = Math.min(d.height, m.height);
					d1.width = Math.min(d.width, m.width);
					return d1;
				} else
					return d;
			}
		};
		messagePane = new JScrollPane(message)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 3605316311642118445L;

			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				Dimension m = getMaximumSize();
				if ( d.height > m.height || d.width > m.width ) {
					Dimension d1 = new Dimension();
					d1.height = Math.min(d.height, m.height);
					d1.width = Math.min(d.width, m.width);
					return d1;
				} else
					return d;
			}
		};
		m_resultPane.setLayout(new GridLayout(1,1));
		m_resultPane.add(messagePane);
		m_resultPane.repaint();
	}
	
	/**
	 * Despliegue de Campos en Layout
	 * @author carlos Parada
	 * @date 07/09/2012
	 * 
	 */
	private void dynInit()
	{
		GroupLayout gl_bm = new GroupLayout(m_DetailPane);
		
		m_ScrollTable.setViewportView(m_Table);
		gl_bm.setHorizontalGroup(
				gl_bm.createParallelGroup()
				.addGroup(gl_bm.createSequentialGroup()
					.addGroup(
						gl_bm.createSequentialGroup()
							.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_lblC_BPartner,50,50,150)
									)
							.addGap(5)
							.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_C_BPartner,200,200,200)
									)
							.addGap(5)
							.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_lblProcessDate,50,50,150)
									)
							.addGap(5)
							.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_ProcessDate,150,150,150)
									)
									.addGap(5)
							.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_lblType,50,50,150)
									)
							.addGap(5)
							.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_Type,200,200,200)
									)							
							.addGap(5)
							.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_lblC_DocType,50,50,150)
									)
							.addGap(5)
							.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_C_DocType,200,200,200)
									)
							.addGap(5)
							)
							).addGap(5)
					
				.addComponent(m_ScrollTable)
				
				);
		gl_bm.setVerticalGroup(gl_bm.createSequentialGroup()
				.addGap(5)
				.addGroup(gl_bm.createSequentialGroup()
						.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(m_lblC_BPartner,25,25,25)
							.addComponent(m_C_BPartner,25,25,25)
							.addComponent(m_lblProcessDate,25,25,25)
							.addComponent(m_ProcessDate,25,25,25)
							.addComponent(m_lblType,25,25,25)
							.addComponent(m_Type,25,25,25)
							.addComponent(m_lblC_DocType,25,25,25)
							.addComponent(m_C_DocType,25,25,25)
								 )
						)
				.addGap(5)
				.addGroup(gl_bm.createSequentialGroup()
						.addGroup(gl_bm.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(m_ScrollTable)
								 )
						)
				.addGap(5)
						);
		m_DetailPane.setLayout(gl_bm);
		
		GroupLayout gl_cp = new GroupLayout(m_ButtomPane);
		
		gl_cp.setHorizontalGroup(
				gl_cp.createParallelGroup()
				.addGroup(gl_cp.createSequentialGroup()
					.addGroup(
							gl_cp.createSequentialGroup()
							.addGroup(gl_cp.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(m_ConfirmPanel)
									)
								)
							)
				
				);
		gl_cp.setVerticalGroup(gl_cp.createSequentialGroup()
				
				.addGroup(gl_cp.createSequentialGroup()
						.addGroup(gl_cp.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(m_ConfirmPanel)
								 )
						 )
							  );
		m_ButtomPane.setLayout(gl_cp);

		m_tabbedPane.add(m_DetailPane, Msg.getMsg(Env.getCtx(), "Select"));
		m_tabbedPane.add(m_resultPane, Msg.getMsg(Env.getCtx(), "Generate"));
	}
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		if (m_frame != null)
			m_frame.dispose();
		m_frame = null;
	}
	
	/**
	 * @author Carlos Parada
	 * Refresca la Tabla
	 */
	private void refresh()
	{
		if (m_C_BPartner.getValue()!=null && m_ProcessDate.getValue()!=null)
			try {
				if (((KeyNamePair)m_Type.getSelectedItem()).getKey()==1)
					loadTable((Integer)m_C_BPartner.getValue(), (Timestamp) m_ProcessDate.getValue(),(DefaultTableModel)m_Table.getModel());
				else
					loadTableIn((Integer)m_C_BPartner.getValue(), (Timestamp) m_ProcessDate.getValue(),(DefaultTableModel)m_Table.getModel());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				ADialog.error(m_WindowNo, m_frame, e.getMessage());
			} catch (ArithmeticException e) {
				// TODO Auto-generated catch block
				ADialog.error(m_WindowNo, m_frame, e.getMessage());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				ADialog.error(m_WindowNo, m_frame, e.getMessage());	
			}
		
	}
	/**
	 * @author carlos Parada
	 * @date 11/09/2012
	 */
	private void selectAll()
	{
		selectAllItems((DefaultTableModel)m_Table.getModel());
	}
	
	@SuppressWarnings("deprecation")
	private void generateDocs()
	{
		
		if (m_C_BPartner.getValue()!=null && m_ProcessDate.getValue()!=null && m_C_DocType.getValue()!=null)
		{
			Vector<String> result =new Vector<String>();
			m_frame.setCursor(Cursor.WAIT_CURSOR);
			
			if (((KeyNamePair)m_Type.getSelectedItem()).getKey()==1)
				result=process((DefaultTableModel)m_Table.getModel(), (Timestamp)m_ProcessDate.getValue(), (Integer)m_C_DocType.getValue(), m_frame, m_WindowNo);
			else
			{
				JOptionPane opt = new JOptionPane();
				if (opt.showConfirmDialog(null, m_C_Charge,Msg.translate(ctx, "C_Charge_ID"),JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
				{
					if (m_C_Charge.getValue()!=null)
						result=processIn((DefaultTableModel)m_Table.getModel(), (Timestamp)m_ProcessDate.getValue(), (Integer)m_C_DocType.getValue(), m_frame, m_WindowNo,Integer.parseInt(m_C_Charge.getValue().toString()));
					else 
					{
						m_frame.setCursor(Cursor.DEFAULT_CURSOR);
						return;
					}
				}
				else
				{
					m_frame.setCursor(Cursor.DEFAULT_CURSOR);
					return ;
				}	
			}
			
			message.setContentType("text/html");
			message.setEditable(false);
			message.setBackground(Color.white);
			message.setFocusable(true);
			
			
			message.setText(createHtml(result));
			
			
			m_tabbedPane.setSelectedIndex(1);
			refresh();
			m_frame.setCursor(Cursor.DEFAULT_CURSOR);
		}
	}
	
	
	/**
	 * Musestra el resultado
	 * @author carlos Parada 
	 * @param result
	 * @return
	 */
	
	public String createHtml (Vector<String> result)
	{
		if (result == null)
			return "";
		//
		StringBuffer sb = new StringBuffer ();
		
		sb.append("<p><font color=\"").append("#0000FF").append("\">** ")
		.append(Msg.translate(ctx, "Created"))
		.append("</font></p>");
		
		sb.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"2\">");
		//
		for (int i = 0; i < result.size(); i++)
		{
			
			sb.append("<tr>");

				sb.append("<td>")
					.append(Msg.translate(ctx,"DocumentNo"))
					.append("</td>");
			//
	
				sb.append("<td>")
					.append(result.get(i))
					.append("</td>");
			
			sb.append("</tr>");
		}

		sb.append("</table>");
		return sb.toString();
	 }	//	createHtml
	

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(m_tabbedPane))
			visibleButtoms();
	}
	/**
	 * @author carlos Parada
	 */
	private void visibleButtoms()
	{
		if (m_tabbedPane.getSelectedIndex()==0)
		{
			m_refreshButton.setVisible(true);
			m_selectAllButton.setVisible(true);
			m_ConfirmPanel.setOKVisible(true);
		}
		else
		{
			m_refreshButton.setVisible(false);
			m_selectAllButton.setVisible(false);
			m_ConfirmPanel.setOKVisible(false);
		}
		
	}
	
	private void setTableModel(boolean isDiscount)
	{

		DefaultTableModel tm = (DefaultTableModel)m_Table.getModel();
		tm.setColumnCount(0);
		if (isDiscount)
		{
			m_Table.addColumn("Select");
			m_Table.addColumn("DocumentNo");
			m_Table.addColumn("DateInvoiced");
			m_Table.addColumn("DueDate");
			m_Table.addColumn("C_PaymentTerm_ID");
			m_Table.addColumn("M_PriceList_Version_ID");
			m_Table.addColumn("DaysRemaining");
			m_Table.addColumn("DaysDue");
			m_Table.addColumn("Discount");
			
			m_Table.setColumnClass(0, Boolean.class, false);
			m_Table.setColumnClass(1, String.class, true, Msg.translate(ctx, "DocumentNo"));
			m_Table.setColumnClass(2, Timestamp.class, true, Msg.translate(ctx, "DateInvoiced"));
			m_Table.setColumnClass(3, Timestamp.class, true, Msg.translate(ctx, "DueDate"));
			m_Table.setColumnClass(4, KeyNamePair.class, true, Msg.translate(ctx, "C_PaymentTerm_ID"));
			m_Table.setColumnClass(5, KeyNamePair.class, true, Msg.translate(ctx, "M_PriceList_Version_ID"));
			m_Table.setColumnClass(6, int.class, true, Msg.translate(ctx, "DaysRemaining"));
			m_Table.setColumnClass(7, int.class, true, Msg.translate(ctx, "DaysDue"));
			m_Table.setColumnClass(8, BigDecimal.class, true, Msg.translate(ctx, "Discount"));
		}
		else
		{
			
			m_Table.addColumn("Select");
			m_Table.addColumn("DocumentNo");
			m_Table.addColumn("DateInvoiced");
			m_Table.addColumn("DueDate");
			m_Table.addColumn("C_PaymentTerm_ID");
			m_Table.addColumn("DaysDue");
			m_Table.addColumn("Interest");
			
			m_Table.setColumnClass(0, Boolean.class, false);
			m_Table.setColumnClass(1, String.class, true, Msg.translate(ctx, "DocumentNo"));
			m_Table.setColumnClass(2, Timestamp.class, true, Msg.translate(ctx, "DateInvoiced"));
			m_Table.setColumnClass(3, Timestamp.class, true, Msg.translate(ctx, "DueDate"));
			m_Table.setColumnClass(4, KeyNamePair.class, true, Msg.translate(ctx, "C_PaymentTerm_ID"));
			m_Table.setColumnClass(5, int.class, true, Msg.translate(ctx, "DaysDue"));
			m_Table.setColumnClass(6, BigDecimal.class, true, Msg.translate(ctx, "Interest"));
		}

		m_Table.autoSize();
	}
	
	private CLabel 			m_lblC_BPartner;
	private CLabel 			m_lblProcessDate;
	private CLabel			m_lblC_DocType;
	private CLabel 			m_lblType;
	private VLookup			m_C_Charge;
	private CComboBox		m_Type;
	private VLookup 		m_C_BPartner;
	private VDate 			m_ProcessDate;
	private VLookup			m_C_DocType;
	private MiniTable 		m_Table;
	private CScrollPane 	m_ScrollTable= new CScrollPane();
	private ConfirmPanel 	m_ConfirmPanel = new ConfirmPanel();
	private int 			m_WindowNo;
	private CPanel 			m_DetailPane = new CPanel();
	private CPanel 			m_ButtomPane = new CPanel();
	private FormFrame 		m_frame;
	private CButton m_selectAllButton;
	private CButton m_refreshButton;
	private CTabbedPane m_tabbedPane= new CTabbedPane();
	private CPanel m_resultPane = new CPanel();
	private StatusBar m_statusBar = new StatusBar();
	private JEditorPane message;
	private JScrollPane messagePane;

}
