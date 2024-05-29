package com.path.simulator.action.channel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TransactionRolledBackException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.net.ssl.HttpsURLConnection;

import com.path.lib.common.util.FileUtil;
import com.pcbsys.nirvana.client.nAbstractChannel;
import com.pcbsys.nirvana.client.nChannelAttributes;
import com.pcbsys.nirvana.client.nConsumeEvent;
import com.pcbsys.nirvana.client.nEventAttributes;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nQueueReaderContext;
import com.pcbsys.nirvana.client.nQueueSyncReader;
import com.pcbsys.nirvana.client.nQueueSyncTransactionReader;
import com.pcbsys.nirvana.client.nSession;
import com.pcbsys.nirvana.client.nSessionAttributes;
import com.pcbsys.nirvana.client.nSessionFactory;
import com.pcbsys.nirvana.client.nTransaction;
import com.pcbsys.nirvana.client.nTransactionAttributes;
import com.pcbsys.nirvana.client.nTransactionFactory;
import com.pcbsys.nirvana.nJMS.ConnectionFactoryImpl;

public class P2P implements Runnable{

	// static final String RNAME = "nhp://remoteHost:443";
//	static final String QUEUE_IN = "VI_001_OUT";   // mock
//    static final String QUEUE_OUT = "VI_001_OUT";
//	static final String RNAME = "nsp://127.0.0.1:9030";

//	static final String RNAME = "nhp://192.168.10.20:9600";		/// server 20 
	static final String RNAME = "nhp://UEAZDBLP000065:9300";  //// local nirvana 
	
//	static final String RNAME = "nhp://192.168.10.20:9610";		/// server 20 Cluster
//	static final String RNAME = "nhp://UEAZDBLP000065:9400";  //// local Cluster
	
	static final String QUEUE= "DH_QUEUE_TST"; // node queue
//	static final String QUEUE = "DH_QUEUE_TST_174"; // node queue
//	static final String QUEUE = "DH_TESTING";// testing queue
	static final String TAG = "pacs.003.001.09_by_p2p_"+ Calendar.getInstance().getTime();

	nSession session = null;
	nQueue queueIn= null;
	P2P(nSession session ,nQueue queueIn){
		this.session = session;
		this.queueIn = queueIn;
	}
	final static String PROPERTIES_FILE_PATH = "JmsConfig.properties";
	static Properties properties = new Properties();
	
	 static {
		 
		 HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> {
			 			//return hostname.equals("127.0.0.1");
			 			return true;
		 });     
	 }
	public static void main(String[] args) throws Exception {
    	
    	String url = "rmi://UEAZDBLP000065:4030";
    	ChannelController channelController = new ChannelController();
    	InputStream inputStream=P2P.class.getResourceAsStream(PROPERTIES_FILE_PATH);
    	// load the properties
//    	properties.load(inputStream); 
    	
//      nQueue queueOut = getQueue(session, QUEUE_OUT);

        //send(dummyMessage(), TAG, queueOut);
        
        
//        send(myBankPac004(), TAG, queueOut);
//        send(sendFrenchMsg(), TAG, queueOut);
//        sendByte(sendFrenchMsgByte(), TAG, queueOut);
    	nSession session = connect(RNAME);
		nQueue queueIn= getQueue(session, QUEUE);
    	P2P p2p = new P2P(session,queueIn);
        ExecutorService taskList  = Executors.newFixedThreadPool(10);
         for (int i=0; i <100; i++) 
         {
//        	 taskList.submit(p2p);
	         taskList.execute(p2p); 
//        	 sendMsgtoQueue(inwardMsg(), TAG, queueIn);
//        	sendMsgtoQueue(sendFrenchMsg(), TAG, queueIn);
         }
         taskList.shutdown();
//         p2p.disconnect(session);
//       	System.exit(-1);
//    	sendMessage("Test Message Body.");
//       sendMessage(sendFrenchMsg());

//        receive(QUEUE_IN);
//        receivedAsJMS();
        System.out.println("done ...");
    }

	@Override
	public void run() {
		try {
			System.out.println( Thread.currentThread().getName() + " start ...");
			sendMsgtoQueue(inwardMsg(), TAG, queueIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String inwardMsg() {
		return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\r\n"
				+ "<Document xmlns=\"pacs.004.001.11_French\">\r\n"
				+ "    <FIToFIPmtStsRpt>\r\n"
				+ "        <GrpHdr>\r\n"
				+ "            <MsgId>" +UUID.randomUUID() + "</MsgId>\r\n"
				+ "            <CreDtTm>2024-01-16T11:28:09.974</CreDtTm>\r\n"
				+ "        </GrpHdr>\r\n"
				+ "        <TxInfAndSts>\r\n"
				+ "            <OrgnlGrpInf>\r\n"
				+ "                <OrgnlMsgId>1000000000062217</OrgnlMsgId>\r\n"
				+ "                <OrgnlMsgNmId>pacs.008.001.09</OrgnlMsgNmId>\r\n"
				+ "            </OrgnlGrpInf>\r\n"
				+ "            <OrgnlInstrId>000000000246</OrgnlInstrId>\r\n"
				+ "            <OrgnlTxId>36220211005000000000246</OrgnlTxId>\r\n"
				+ "            <TxSts>RJCT</TxSts>\r\n"
				+ "            <StsRsnInf>\r\n"
				+ "                <Orgtr>\r\n"
				+ "                    <Nm>SCPI</Nm>\r\n"
				+ "                </Orgtr>\r\n"
				+ "                <Rsn>\r\n"
				+ "                    <Prtry>" + UUID.randomUUID() + " TEST Party épassé épassé épassé</Prtry>\r\n"
				+ "                </Rsn>\r\n"
				+ "                <AddtlInf> " + UUID.randomUUID() + " Délai dépassé.</AddtlInf>\r\n"
				+ "            </StsRsnInf>\r\n"
				+ "            <ClrSysRef>362263MAD00000220240116000000000246</ClrSysRef>\r\n"
				+ "            <InstgAgt>\r\n"
				+ "                <FinInstnId>\r\n"
				+ "                    <ClrSysMmbId>\r\n"
				+ "                        <ClrSysId>\r\n"
				+ "                            <Prtry>TEST Party</Prtry>\r\n"
				+ "                        </ClrSysId>\r\n"
				+ "                        <MmbId>263</MmbId>\r\n"
				+ "                    </ClrSysMmbId>\r\n"
				+ "                </FinInstnId>\r\n"
				+ "            </InstgAgt>\r\n"
				+ "            <InstdAgt>\r\n"
				+ "                <FinInstnId>\r\n"
				+ "                    <ClrSysMmbId>\r\n"
				+ "                        <ClrSysId>\r\n"
				+ "                            <Prtry>GSIMT</Prtry>\r\n"
				+ "                        </ClrSysId>\r\n"
				+ "                        <MmbId>362</MmbId>\r\n"
				+ "                    </ClrSysMmbId>\r\n"
				+ "                </FinInstnId>\r\n"
				+ "            </InstdAgt>\r\n"
				+ "        </TxInfAndSts>\r\n"
				+ "    </FIToFIPmtStsRpt>\r\n"
				+ "</Document>" ;
	}

	private static byte[] sendFrenchMsgByte() throws Exception {

		String filePath = "E:\\1_Digital_Hub_Project_26Jan2021\\PACS_XSD_Files\\xml_message_pac007.xml";
//    	String filePath = "E:\\1_Digital_Hub_Project_26Jan2021\\PACS_XSD_Files\\TestMsg.xml";
//    	String filePath = "E:\\1_Digital_Hub_Project_26Jan2021\\PACS_XSD_Files\\testFrench.xml";
		byte[] fileByte = FileUtil.readFileBytes(filePath);
//	    String str = new String(fileByte,StandardCharsets.ISO_8859_1);
//	    String str2 = new String(fileByte,StandardCharsets.UTF_8);
		return fileByte;
	}

	private static String sendFrenchMsg() throws Exception {
    	String filePath = "E:\\1_Digital_Hub_Project_26Jan2021\\PACS_XSD_Files\\TestMsg.xml";
//		String filePath = "E:\\1_Digital_Hub_Project_26Jan2021\\PACS_XSD_Files\\testFrench.xml";
		String fileData = FileUtil.readFile(filePath, "ISO-8859-1");
//	    String str = new String(fileByte,StandardCharsets.ISO_8859_1);
//	    String str2 = new String(fileByte,StandardCharsets.UTF_8);
		return fileData;
	}

	static void sendTextMsg(String textMessage, String tag, nAbstractChannel queue) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    	baos.write(textMessage.getBytes(StandardCharsets.UTF_8));
		baos.write(textMessage.getBytes(StandardCharsets.ISO_8859_1));

		nEventAttributes attributes = new nEventAttributes();
		attributes.setMessageType(nEventAttributes.JMS_TEXT_MESSAGE_TYPE);
		nConsumeEvent evt = new nConsumeEvent("Message", baos.toByteArray());
		evt.setAttributes(attributes);

		nTransactionAttributes tattrib = new nTransactionAttributes(queue);
		nTransaction myTransaction = nTransactionFactory.create(tattrib);
		myTransaction.publish(evt);
		myTransaction.commit();

	}

	static void sendByte(byte[] messagebyte, String tag, nAbstractChannel queue) throws Exception {

		nTransactionAttributes tattrib = new nTransactionAttributes(queue);
		nTransaction myTransaction = nTransactionFactory.create(tattrib);
		nConsumeEvent evt = new nConsumeEvent(tag, messagebyte);
		nEventAttributes evtAttrs = new nEventAttributes();
		evtAttrs.setMessageType((byte) 2);
		evt.setAttributes(evtAttrs);

		myTransaction.publish(evt);
		myTransaction.commit();

	}

	/**
	 * Envoie le message accompagné de son tag (ISO 20022) vers la queue
	 *
	 * @param Message
	 * @param Tag
	 * @param Queue   destinataire
	 * @throws Exception
	 */
	static void send(String message, String tag, nAbstractChannel queue) throws Exception {

		nTransactionAttributes tattrib = new nTransactionAttributes(queue);
		nTransaction myTransaction = nTransactionFactory.create(tattrib);
		nConsumeEvent evt = new nConsumeEvent(tag, message.getBytes());
		nEventAttributes evtAttrs = new nEventAttributes();
		evtAttrs.setMessageType((byte) 5);
		evt.setAttributes(evtAttrs);

		myTransaction.publish(evt);
		myTransaction.commit();

	}

	public void sendMsgtoQueue(String message, String tag, nAbstractChannel queue) throws Exception {
		System.out.println( Thread .currentThread() .getName() + " " +  Calendar.getInstance().getTime()); 
		nTransactionAttributes tattrib = new nTransactionAttributes(queue);
		nTransaction myTransaction = nTransactionFactory.create(tattrib);
		nConsumeEvent evt = new nConsumeEvent(tag, message.getBytes(StandardCharsets.ISO_8859_1));
		nEventAttributes evtAttrs = new nEventAttributes();
		evtAttrs.setMessageType(nEventAttributes.JMS_BYTES_MESSAGE_TYPE);
		evt.setAttributes(evtAttrs);

		myTransaction.publish(evt);
		myTransaction.commit();

	}

	private static void sendMessage(String body)
			throws JMSException, IOException, InterruptedException, NamingException {
		// get the initial context
//      

//    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		dbf.setNamespaceAware(true);
//		Document inputDocument = dbf.newDocumentBuilder().parse(new FileInputStream("E:\\1_Digital_Hub_Project_26Jan2021\\PACS_XSD_Files\\TestMsg.xml"));
//      nConsumeEvent evt = new nConsumeEvent(tag,inputDocument);

//    	String brokerUrl = "nsp://192.168.10.32:9000,nsp://192.168.10.20:9000";

		// Connection connection = cf.createConnection();
		// ConnectionFactoryImpl cf = new ConnectionFactoryImpl();

		boolean transacted = true;

		Hashtable<String, Object> env = new Hashtable<>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, properties.getProperty(Context.INITIAL_CONTEXT_FACTORY ,"com.pcbsys.nirvana.nSpace.NirvanaContextFactory"));
//		env.put(Context.PROVIDER_URL, properties.getProperty(Context.PROVIDER_URL ,RNAME));
		env.put(Context.PROVIDER_URL, properties.getProperty(Context.PROVIDER_URL ,RNAME));

		//	nhps://UEAZDBLP000065:9301,nhps://192.168.10.20:9601
		
		env.put("nirvana.ssl.keystore.path", new File(properties.getProperty("nirvana.ssl.keystore.path","C:/keystore/clientKeystore.jks") ).getAbsolutePath());
		env.put("nirvana.ssl.keystore.pass", properties.getProperty("nirvana.ssl.keystore.pass","nirvana"));
		env.put("nirvana.ssl.keystore.cert", properties.getProperty("nirvana.ssl.keystore.cert","nirvana"));
		// Certificate alias for the client to use when connecting to an interface
		// with client validation enabled
		env.put("nirvana.ssl.truststore.path", new File(properties.getProperty("nirvana.ssl.truststore.path","C:/keystore/clientTruststore.jks")).getAbsolutePath());
		env.put("nirvana.ssl.truststore.pass", properties.getProperty("nirvana.ssl.truststore.pass","nirvana"));
		env.put("nirvana.ssl.protocol", properties.getProperty("nirvana.ssl.protocol","TLS"));
		env.put(Context.SECURITY_AUTHENTICATION, properties.getProperty(Context.SECURITY_AUTHENTICATION ,"simple"));
		env.put(Context.SECURITY_PRINCIPAL,properties.getProperty(Context.SECURITY_PRINCIPAL ,"nirvana"));
		env.put(Context.SECURITY_CREDENTIALS, properties.getProperty(Context.SECURITY_CREDENTIALS ,"nirvana"));


//		String conectionfactoryName = properties.getProperty("jndi.name" , "DH_CON_FACT");
		String queueName = properties.getProperty("queue.out" , QUEUE);
		
//		System.out.println("jndi.name ::: " + properties.getProperty("jndi.name") );
//		System.out.println("queue.out ::: " +  properties.getProperty("queue.out") );
//		InitialContext initialContext = new InitialContext(env);
//		ConnectionFactory cf = (ConnectionFactory) initialContext.lookup(conectionfactoryName);

//		ConnectionFactoryImpl cf = new ConnectionFactoryImpl("nhps://192.168.10.20:9601");
          ConnectionFactoryImpl cf = new ConnectionFactoryImpl(RNAME);
    	  cf.setProperties(env);
    	  
		// Create a Connection from the Connection Factory
       	 cf.setReconnectInterval(5 * 1000L);
       	 cf.setImmediateReconnect(true);
       	 cf.setDynamicProperties(env);
       	 cf.setMaxReconAttempts(10);

       	cf.setAutoReconnectAfterACL(true);
       	cf.setDisconnectOnClusterFailure(false);
       	cf.setImmediateReconnect(true);
       	cf.setFollowMaster(true);
		
		/// create connection by initial context
		for(Entry<String, Object> entry : env.entrySet())
		{	
			System.out.println("property name ::: " +  entry.getKey() + " ::: value ::: " + entry.getValue() );
		}
		
		/// evn need to set when protocol is SSL or needs to connect by username &
		/// password
		 Connection c = cf.createConnection();
		// Start the connection
		c.start();
		// Create a Sesson from the Connection
		Session s = c.createSession(transacted, 1);

		// get the initial context
//          Context ctx = getInitialContext();
		// create the destination, and bind if necessary
//          Destination d = getDestination(ctx, s, destName );
		Destination d = s.createQueue(queueName); /// getDestination(ctx, s, destName );

		// Create a Producer from the Session
		MessageProducer p = s.createProducer(d);

		// Prompt for a message
//          BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 1);
//          System.out.print("Enter a message to be published : ");
//          String str = br.readLine();

		// create a bytes message
		TextMessage tmsg = s.createTextMessage();
		tmsg.setText(body);
		try {
			// Publish the message to the topic
			p.send(tmsg);
		} catch (JMSException e) {
			e.printStackTrace();
			boolean committed = false;

			while (!committed) {
				try {
					p.send(tmsg);
					committed = true;
				} catch (JMSException ex) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ignored) {
					}
				}
			}
		}

//           BytesMessage bmsg = s.createBytesMessage();
//           bmsg.writeBytes(body.getBytes(StandardCharsets.ISO_8859_1));
//           p.send(bmsg);
//           
		// Loop for count
//          for (int x = 0; x < count; x++) {
//            p.send(bmsg);
//          }

		// Print a message to the console saying we are about to exit
		System.out.println("Closing session and connection. Published a messages");
		Thread.sleep(1000);
		c.close();
		s.close();
		System.exit(-1);
//          ctx.close();
	}

	/**
	 * Recupere le message posté par l'expediteur
	 *
	 * @param Queue
	 * @throws Exception
	 */
	static String receive(nQueue queue) throws Exception {
		nQueueSyncReader reader = queue.createTransactionalReader(new nQueueReaderContext());
		nConsumeEvent evt = reader.pop(1);
		if (evt != null) {
			long id = evt.getEventID();
			String tag = evt.getEventTag();
			byte[] data = evt.getEventData();
			String messageISO = new String(data, StandardCharsets.ISO_8859_1);
			String message = new String(data, StandardCharsets.UTF_8);
			((nQueueSyncTransactionReader) reader).commit(id);
			return "Message taggé " + tag + " contenant <" + message + ">" + " ::: ISO ::: " + messageISO;

		}
		return "Queue vide";
	}

	static void receivedAsJMS() {
		try {
			QueueConnectionFactory qcf = new com.pcbsys.nirvana.nJMS.QueueConnectionFactoryImpl("nhps://UEAZDBLP000065:9301");
			QueueConnection qc = qcf.createQueueConnection();
//          qc.setExceptionListener(new nJMSExceptionList);
			qc.start();
			QueueSession qs = qc.createQueueSession(true, Session.SESSION_TRANSACTED);
			Queue q = qs.createQueue(QUEUE);
			QueueReceiver qr = qs.createReceiver(q);
			boolean run = true;
			int i = 0;
			while (run) {
				Message receivedMsg = qr.receive();
				if (receivedMsg instanceof TextMessage) {
					TextMessage tmsg = (TextMessage) qr.receive();

					System.out.println("JMS MSG TYPE : " + tmsg.getJMSType());
					System.out.println("JMS MSG ID : " + tmsg.getJMSMessageID());
					System.out.println("JMS DELIVERY MODE : " + tmsg.getJMSDeliveryMode());
					System.out.println("JMS TIME STAMP : " + tmsg.getJMSTimestamp());
					System.out.println("Request : " + tmsg.getText());

//                byte[] msgByte = tmsg.getText().getBytes(StandardCharsets.ISO_8859_1);
//                String messageISO = new String(msgByte, StandardCharsets.ISO_8859_1);
//                String message = new String(msgByte, StandardCharsets.UTF_8);
//                System.out.println("Message taggé  contenant <" + message + ">" + " ::: ISO ::: " + messageISO ) ;

				} else if (receivedMsg instanceof TextMessage) {
					BytesMessage bytesMessage = (BytesMessage) qr.receive();
					long size = bytesMessage.getBodyLength();
					byte[] myBytes = new byte[(int) size];
					bytesMessage.readBytes(myBytes);
					String myString = new String(myBytes, StandardCharsets.UTF_8);
					String isoString = new String(myBytes, StandardCharsets.ISO_8859_1);
					System.out.println("BytesMessage msg ::::: " + myString + " ::: ISO ::: " + isoString);

				}

				boolean committed = false;
				while (!committed) {
					try {
						qs.commit();
						committed = true;
//                run=false;
					} catch (TransactionRolledBackException e) {
						qs.commit();
						committed = true;
						System.out.println("Transaction rolled back " + e.getMessage());
//                run=false;
					} catch (Exception e) {
						qs.commit();
						Thread.sleep(1000);
						e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
//                run=false;
					}
				}

				i++;
				if (i >= 1000)
					run = false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//    import com.pcbsys.nirvana.client.nConcurrentConnectionFactory;
//    import org.springframework.context.annotation.Bean;
//    import org.springframework.context.annotation.Configuration;
//    import org.springframework.integration.jms.DefaultJmsHeaderMapper;
//    import org.springframework.integration.jms.JmsHeaderMapper;
//    import org.springframework.jms.connection.CachingConnectionFactory;
//    import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
//     
//    import javax.jms.ConnectionFactory;

//     public ConnectionFactory jmsConnectionFactory() {
//            // Create a Nirvana connection factory
//            nConcurrentConnectionFactory nFactory = new nConcurrentConnectionFactory("tcp://nirvana-server-host1:9000,tcp://nirvana-server-host2:9000,tcp://nirvana-server-host3:9000");
//            // Optionally, configure other properties of nFactory
//            // Wrap it with Spring's CachingConnectionFactory
//            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
//            cachingConnectionFactory.setTargetConnectionFactory(nFactory);
//     
//            // Wrap it with UserCredentialsConnectionFactoryAdapter if needed
//            UserCredentialsConnectionFactoryAdapter connectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
//            connectionFactoryAdapter.setTargetConnectionFactory(cachingConnectionFactory);
//            connectionFactoryAdapter.setUsername("your-username");
//            connectionFactoryAdapter.setPassword("your-password");
//     
//            return connectionFactoryAdapter;
//        
//    }

	/**
	 * Recupere une instance de connexion de QUEUE
	 *
	 * @param Session
	 * @param NomDeLaQueue
	 * @return Queue
	 * @throws Exception
	 */
	static nQueue getQueue(nSession session, String queueName) throws Exception {
		nChannelAttributes cattrib = new nChannelAttributes();
		cattrib.setName(queueName);
		nQueue queue = session.findQueue(cattrib);
		System.out.println("Queue Retrieved: " + queue.getName());

		return queue;
	}

	/**
	 * Connexion et obtention d'une session
	 *
	 * @param rnames
	 * @return session
	 * @throws Exception
	 */
	static nSession connect(String rnames) throws Exception {
		nSessionAttributes sessionAttributes = new nSessionAttributes(rnames);
		nSession connectionObject = nSessionFactory.create(sessionAttributes);
		connectionObject.init();
		return connectionObject;
	}

	/**
	 * Liberation de la session
	 *
	 * @param session
	 */
	void disconnect(nSession session) {
		session.close();
		session = null;
		System.out.println("session disconnect... ");
	}

	static String dummyMessage() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + "<FIToFICstmrDrctDbt>\r\n"
				+ "    <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\r\n" + "        <SignedInfo>\r\n"
				+ "            <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\r\n"
				+ "            <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\r\n"
				+ "            <Reference>\r\n"
				+ "                <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\r\n"
				+ "                <DigestValue>47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=</DigestValue>\r\n"
				+ "            </Reference>\r\n"
				+ "            <Reference URI=\"#07f3a59f-03f9-4b0c-8a76-c3f111aa0359\">\r\n"
				+ "                <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\r\n"
				+ "                <DigestValue>47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=</DigestValue>\r\n"
				+ "            </Reference>\r\n" + "        </SignedInfo>\r\n"
				+ "        <SignatureValue>Wqxz8uzoUmgZ5u3hmAfa3s73vJj2JB521TQfgle7otjaRikbAAwq5XGlWBxDxR8U3Jdx+MwZNCkT\r\n"
				+ "oAGoK2y3/pf9i0jMV15KGnLz0CjTrXc3GoJVejpTyoSdOTPlfnoN3kFwy/ZQx3bMGs/JillQUALq\r\n"
				+ "ksEImED7FFK2eNPekKOiG0pupIdHdomfJ1fgJ/GiCGVzmhVIrBUAg9nkI+4D/WPbucJ02VYslBJb\r\n"
				+ "QCGbTiORYP48/tW3u2jUR3/tvoMwoAbEsOkHTa5JwFx8V4UONwGwPcRMYkzPUkiMuRF7TPDOxoXK\r\n"
				+ "VEOGwIeYbMFBqnOzkPe1ZryBSeZOqdSlX+68JL0+UtxvdHFzjQ5va4IDwSyzVQy7gv0vVbioEYT3\r\n"
				+ "QffSeIhrVko7xIA+pqkZiFO0y2fB8ocJb1NM6Up44DBeDtsZh5ahB6UJLSgUg/3ZZJjG+QzqQmHM\r\n"
				+ "dx7TNIWc0sHxzeW9XR/U2hF0U65AVMEwRphTPVqyQyPZgvprJQeQWA/ryrf9tHWBo21L29LFDAHB\r\n"
				+ "uvR07t+SJAaYlLXP2siteMGLyj2Q4b5tQLYfiuZf+rGkxQeWP6dY5onLCnP135mRuOsIUvIh3/cE\r\n"
				+ "sesmEdJog4n7F7R2rP67n37ohLHY0gBADE0Zq3xecR+es1Jd0euzGuuhzPbb/05RJq75prOGqtI=</SignatureValue>\r\n"
				+ "        <KeyInfo Id=\"07f3a59f-03f9-4b0c-8a76-c3f111aa0359\">\r\n" + "            <X509Data>\r\n"
				+ "                <X509Certificate>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</X509Certificate>\r\n"
				+ "            </X509Data>\r\n" + "        </KeyInfo>\r\n" + "    </Signature>\r\n" + "    <GrpHdr>\r\n"
				+ "        <Authstn>\r\n" + "            <Cd>SYSADM</Cd>\r\n" + "            <Prtry>1</Prtry>\r\n"
				+ "        </Authstn>\r\n" + "        <CreDtTm>1</CreDtTm>\r\n" + "        <MsgId>SYSADM</MsgId>\r\n"
				+ "        <NbOfTxs>1</NbOfTxs>\r\n" + "        <BtchBookg>1</BtchBookg>\r\n" + "    </GrpHdr>\r\n"
				+ "</FIToFICstmrDrctDbt>";
	}

	static String dummyMessage(int i) {
		return "<FIToFICstmrDrctDbt xmlns=\"pacs.003.001.09\" \r\n"
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \r\n"
				+ "xsi:schemaLocation=\"pacs.003.001.09\">\r\n" + "				    <GrpHdr>\r\n"
				+ "				      <MsgId>str1234-" + i + " </MsgId>\r\n"
				+ "				      <CreDtTm>2012-12-13T12:12:12</CreDtTm>\r\n"
				+ "				      <NbOfTxs>str1234</NbOfTxs>\r\n" + "				      <SttlmInf>\r\n"
				+ "				        <SttlmMtd>INDA</SttlmMtd>\r\n" + "				      </SttlmInf>\r\n"
				+ "				    </GrpHdr>\r\n" + "				    <DrctDbtTxInf>\r\n"
				+ "				      <PmtId>\r\n" + "				        <EndToEndId>str1234</EndToEndId>\r\n"
				+ "				      </PmtId>\r\n"
				+ "				      <IntrBkSttlmAmt Ccy=\"str1234\">123.45</IntrBkSttlmAmt>\r\n"
				+ "				      <ChrgBr>DEBT</ChrgBr>\r\n" + "				      <Cdtr />\r\n"
				+ "				      <CdtrAgt>\r\n" + "				        <FinInstnId />\r\n"
				+ "				      </CdtrAgt>\r\n" + "				      <Dbtr />\r\n"
				+ "				      <DbtrAcct />\r\n" + "				      <DbtrAgt>\r\n"
				+ "				        <FinInstnId />\r\n" + "				      </DbtrAgt>\r\n"
				+ "				    </DrctDbtTxInf>\r\n" + "	</FIToFICstmrDrctDbt>" + "";
	}

	static String myBankPac004() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
				+ "<DataPDU xmlns=\"urn:swift:saa:xsd:saa.2.0\">\r\n" + "    <Header>\r\n" + "        <Message>\r\n"
				+ "            <SenderReference>1</SenderReference>\r\n"
				+ "            <MessageIdentifier>1</MessageIdentifier>\r\n" + "            <Format>AnyXML</Format>\r\n"
				+ "            <Sender>\r\n" + "                <BIC12>1</BIC12>\r\n" + "                <FullName>\r\n"
				+ "                    <X1>test</X1>\r\n" + "                </FullName>\r\n"
				+ "            </Sender>\r\n" + "            <Receiver>\r\n" + "                <BIC12>1</BIC12>\r\n"
				+ "                <FullName>\r\n" + "                    <X1>1</X1>\r\n"
				+ "                </FullName>\r\n" + "            </Receiver>\r\n" + "            <InterfaceInfo>\r\n"
				+ "                <UserReference>1</UserReference>\r\n" + "            </InterfaceInfo>\r\n"
				+ "            <NetworkInfo>\r\n" + "                <Priority>Normal</Priority>\r\n"
				+ "                <Network>Application</Network>\r\n" + "            </NetworkInfo>\r\n"
				+ "        </Message>\r\n" + "    </Header>\r\n" + "    <Body>\r\n" + "        <AppHdr>\r\n"
				+ "            <Fr>\r\n" + "                <FIId>\r\n" + "                    <FinInstnId>\r\n"
				+ "                        <BICFI>test</BICFI>\r\n" + "                    </FinInstnId>\r\n"
				+ "                </FIId>\r\n" + "            </Fr>\r\n" + "            <To>\r\n"
				+ "                <FIId>\r\n" + "                    <FinInstnId>\r\n"
				+ "                        <BICFI>1</BICFI>\r\n" + "                    </FinInstnId>\r\n"
				+ "                </FIId>\r\n" + "            </To>\r\n" + "            <BizMsgIdr>1</BizMsgIdr>\r\n"
				+ "            <MsgDefIdr>pacs.004.001.10</MsgDefIdr>\r\n" + "        </AppHdr>\r\n"
				+ "        <Document>\r\n" + "            <PmtRtr>\r\n" + "                <GrpHdr>\r\n"
				+ "                    <MsgId>1</MsgId>\r\n" + "                    <CreDtTm>1</CreDtTm>\r\n"
				+ "                    <NbOfTxs>1</NbOfTxs>\r\n" + "                    <SttlmInf>\r\n"
				+ "                        <SttlmMtd>1</SttlmMtd>\r\n" + "                    </SttlmInf>\r\n"
				+ "                </GrpHdr>\r\n" + "                <TxInf>\r\n"
				+ "                    <RtrId>1</RtrId>\r\n" + "                    <OrgnlGrpInf>\r\n"
				+ "                        <OrgnlMsgId>1</OrgnlMsgId>\r\n"
				+ "                        <OrgnlMsgNmId>1</OrgnlMsgNmId>\r\n"
				+ "                        <OrgnlCreDtTm>1</OrgnlCreDtTm>\r\n"
				+ "                    </OrgnlGrpInf>\r\n"
				+ "                    <OrgnlEndToEndId>1</OrgnlEndToEndId>\r\n"
				+ "                    <OrgnlTxId>1</OrgnlTxId>\r\n"
				+ "                    <OrgnlUETR>1</OrgnlUETR>\r\n"
				+ "                    <OrgnlClrSysRef>1</OrgnlClrSysRef>\r\n"
				+ "                    <OrgnlIntrBkSttlmAmt>1</OrgnlIntrBkSttlmAmt>\r\n"
				+ "                    <OrgnlIntrBkSttlmDt>1</OrgnlIntrBkSttlmDt>\r\n"
				+ "                    <RtrdIntrBkSttlmAmt>1</RtrdIntrBkSttlmAmt>\r\n"
				+ "                    <SttlmTmIndctn>\r\n" + "                        <DbtDtTm>1</DbtDtTm>\r\n"
				+ "                    </SttlmTmIndctn>\r\n" + "                    <XchgRate>1</XchgRate>\r\n"
				+ "                    <ChrgsInf>\r\n" + "                        <Amt>1</Amt>\r\n"
				+ "                        <Agt>\r\n" + "                            <FinInstnId>\r\n"
				+ "                                <BICFI>1</BICFI>\r\n"
				+ "                            </FinInstnId>\r\n" + "                        </Agt>\r\n"
				+ "                    </ChrgsInf>\r\n" + "                    <ClrSysRef>1</ClrSysRef>\r\n"
				+ "                    <InstgAgt>\r\n" + "                        <FinInstnId>\r\n"
				+ "                            <BICFI>test</BICFI>\r\n" + "                        </FinInstnId>\r\n"
				+ "                    </InstgAgt>\r\n" + "                    <InstdAgt>\r\n"
				+ "                        <FinInstnId>\r\n" + "                            <BICFI>1</BICFI>\r\n"
				+ "                        </FinInstnId>\r\n" + "                    </InstdAgt>\r\n"
				+ "                </TxInf>\r\n" + "            </PmtRtr>\r\n" + "        </Document>\r\n"
				+ "    </Body>\r\n" + "</DataPDU>";

	}

}
