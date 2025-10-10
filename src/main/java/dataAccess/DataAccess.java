package dataAccess;


import java.io.File;


import java.net.NoRouteToHostException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import exceptions.alertAlreadyExists;
import domain.User;
import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Admin;
import domain.Alerta;
import domain.Balorazio;
import domain.Book;
import domain.Driver;
import domain.Erreklamazio;
import domain.Kotxe;
import domain.Mugimendua;
import domain.Ride;
import domain.Traveler;
import exceptions.MoneyDatabaseException;
import exceptions.NoCashException;
import exceptions.NotEnoughSeatsException;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.bookAlreadyExistException;
import exceptions.carAlreadyExistsException;
import exceptions.erreklamazioAlreadyExistsException;
import exceptions.moreThanCarSeatsException;
import exceptions.ratingMoreThanFiveException;
import exceptions.reviewAlreadyExistsException;
import exceptions.rideIsNotConfirmed;

/**
 * It implements the data access to the objectDb database
 */
public class DataAccess  {
	private  EntityManager  db;
	private  EntityManagerFactory emf;


	ConfigXML c=ConfigXML.getInstance();

     public DataAccess()  {
    
    	
		if (c.isDatabaseInitialized()) {
			String fileName=c.getDbFilename();

			File fileToDelete= new File(fileName);
			if(fileToDelete.delete()){
				File fileToDeleteTemp= new File(fileName+"$");
				fileToDeleteTemp.delete();

				  System.out.println("File deleted");
				} else {
				  System.out.println("Operation failed");
				}
		}
		
		open();
		if  (c.isDatabaseInitialized())initializeDB();
		
		System.out.println("DataAccess created => isDatabaseLocal: "+c.isDatabaseLocal()+" isDatabaseInitialized: "+c.isDatabaseInitialized());

		close();
	
		
		

	}
     
    public DataAccess(EntityManager db) {
    	this.db=db;
    }

	
	
	/**
	 * This is the data access method that initializes the database with some events and questions.
	 * This method is invoked by the business logic (constructor of BLFacadeImplementation) when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	public void initializeDB(){
		
		db.getTransaction().begin();

		try {

		   Calendar today = Calendar.getInstance();
		   
		   int month=today.get(Calendar.MONTH);
		   int year=today.get(Calendar.YEAR);
		   if (month==12) { month=1; year+=1;}  
	    
		   
		    //Create drivers 
			User driver1=new Driver("Y234G1","11", "99","driver1@gmail.com","Pedrerol", "Fernandez", "30/10/1989", 1,"Gizona");
			User driver2=new Driver("Y234G2","22", "33","driver12@gmail.com","Iker", "Fernandez", "30/10/1989", 2,"Gizona");
			User traveler1=new Traveler("Y234G3","33", "44","driver13@gmail.com","Pedro", "Fernandez", "30/10/1989", 3,"Gizona");
			User traveler2=new Traveler("Y234G4","44", "55","driver14@gmail.com","Messi", "Fernandez", "30/10/1989", 4,"Gizona");
			User traveler3=new Traveler("Y234G45","88", "99","driver14@gmail.com","Aitor", "Fernandez", "30/10/1989", 4,"Gizona");
			User admin1=new Admin ("Y234G458","admin", "admin","admin14@gmail.com","Admin", "Admin", "30/10/1989", 4,"Gizona");
			
			Traveler traveler33=(Traveler) traveler3;
			Traveler traveler22=(Traveler) traveler2;
			//traveler33.setDirua(20);
			//Create rides
			Driver driver11= (Driver) driver1;
			
			Kotxe k1=driver11.addCar("Audi","a4",5,"2563GHB");
			
			
			Ride r11= driver11.addRide("Donostia", "Bilbo", UtilDate.newDate(year,month,15), 4, 7,k1);
			//Integer bookNumber, Traveler traveler, Ride ride, int eserlekuKop
			k1.addRide(r11);
			Ride r22 =driver11.addRide("Donostia", "Gazteiz", UtilDate.newDate(year,month,6), 4, 8,k1);
			k1.addRide(r22);
			Ride r33= driver11.addRide("Bilbo", "Donostia", UtilDate.newDate(year,month,25), 4, 4,k1);
			k1.addRide(r33);
			
			
			/**
			Ride r1= new Ride("Donostia", "Bilbo", UtilDate.newDate(year,month,15), 4, 7, driver11);
			driver11.getRideList().add(r1);
			*/
			/**
			Book b1=new Book(traveler33,r1,3);
			Book b2=new Book(traveler22,r2,4);
			Book b3=new Book(traveler33,r3,5);
			r1.addBook(b1);
			r2.addBook(b2);
			r3.addBook(b3);
			*/
			
			Driver driver22=(Driver) driver2;
			Kotxe k2=driver22.addCar("Audi", "rs6", 4, "9283JNM");
			Ride r1=driver22.addRide("Donostia", "Bilbo", UtilDate.newDate(year,month,15), 3, 3,k2);
			k2.addRide(r1);
			Ride r2=driver22.addRide("Bilbo", "Donostia", UtilDate.newDate(year,month,25), 2, 5,k2);
			k2.addRide(r2);
			Ride r3= driver22.addRide("Eibar", "Gasteiz", UtilDate.newDate(year,month,6), 2, 5,k2);
			k2.addRide(r3);
			
			db.persist(driver1);
			db.persist(driver2);
			db.persist(traveler1);
			db.persist(traveler22);
			db.persist(traveler33);
			db.persist(admin1);
	
			db.getTransaction().commit();
			System.out.println("Db initialized");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns all the cities where rides depart 
	 * @return collection of cities
	 */
	public List<String> getDepartCities(){
			TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.from FROM Ride r ORDER BY r.from", String.class);
			List<String> cities = query.getResultList();
			return cities;
		
	}
	/**
	 * This method returns all the arrival destinations, from all rides that depart from a given city  
	 * 
	 * @param from the depart location of a ride
	 * @return all the arrival destinations
	 */
	public List<String> getArrivalCities(String from){
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.to FROM Ride r WHERE r.from=?1 ORDER BY r.to",String.class);
		query.setParameter(1, from);
		List<String> arrivingCities = query.getResultList(); 
		return arrivingCities;
		
	}
	/**
	 * This method creates a ride for a driver
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @param nPlaces available seats
	 * @param driverEmail to which ride is added
	 * 
	 * @return the created ride, or null, or an exception
	 * @throws RideMustBeLaterThanTodayException if the ride date is before today 
 	 * @throws RideAlreadyExistException if the same ride already exists for the driver
	 */
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String NAN, String matrikula) throws  RideAlreadyExistException, RideMustBeLaterThanTodayException,moreThanCarSeatsException {
		System.out.println(">> DataAccess: createRide=> from= "+from+" to= "+to+" driver="+NAN+" date "+date);
		try {
			if(new Date().compareTo(date)>0) {
				throw new RideMustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.ErrorRideMustBeLaterThanToday"));
			}
			db.getTransaction().begin();
			
			Driver driver = db.find(Driver.class,NAN );
			Kotxe k=db.find(Kotxe.class, matrikula);
			if (driver.doesRideExists(from, to, date)) {
				db.getTransaction().commit();
				throw new RideAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.RideAlreadyExist"));
			}
			if(k.getEserlekuKopurua()<nPlaces) {
				db.getTransaction().commit();
				throw new moreThanCarSeatsException(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.incorrectSeats"));
			}
			Ride ride = driver.addRide(from, to, date, nPlaces, price,k);
			k.addRide(ride);
			System.out.println("Se ha guardado correctamente");
			//next instruction can be obviated
			db.persist(driver);
			db.getTransaction().commit();

			return ride;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			db.getTransaction().commit();
			return null;
		}
		
		
	}
	
	/**
	 * This method retrieves the rides from two locations on a given date 
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @return collection of rides
	 */
	public List<Ride> getRidesFromDriver(String NAN){
		List<Ride> rides=null;
		try {
			db.getTransaction().begin();
			Driver driver=db.find(Driver.class, NAN);
			rides=driver.getRideList();
			db.getTransaction().commit();
			return rides;
		}catch (NullPointerException e) {
			// TODO Auto-generated catch block
			db.getTransaction().commit();
			return null;
		}
	}
	public List<Book> getBooksFromRide(Integer rideNumber){
		List<Book> books=null;
		try {
			db.getTransaction().begin();
			Ride ride=db.find(Ride.class, rideNumber);
			books=ride.getBookList();
			db.getTransaction().commit();
			return books;
		}catch (NullPointerException e) {
			// TODO Auto-generated catch block
			db.getTransaction().commit();
			return null;
		}
	}
	public List<Ride> getRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getRides=> from= "+from+" to= "+to+" date "+date);

		List<Ride> res = new ArrayList<>();	
		TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r WHERE r.from=?1 AND r.to=?2 AND r.date=?3",Ride.class);   
		query.setParameter(1, from);
		query.setParameter(2, to);
		query.setParameter(3, date);
		List<Ride> rides = query.getResultList();
	 	 for (Ride ride:rides){
		   res.add(ride);
		  }
	 	return res;
	}
	
	/**
	 * This method retrieves from the database the dates a month for which there are events
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride 
	 * @param date of the month for which days with rides want to be retrieved 
	 * @return collection of rides
	 */
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getEventsMonth");
		List<Date> res = new ArrayList<>();	
		
		Date firstDayMonthDate= UtilDate.firstDayMonth(date);
		Date lastDayMonthDate= UtilDate.lastDayMonth(date);
				
		
		TypedQuery<Date> query = db.createQuery("SELECT DISTINCT r.date FROM Ride r WHERE r.from=?1 AND r.to=?2 AND r.date BETWEEN ?3 and ?4",Date.class);   
		
		query.setParameter(1, from);
		query.setParameter(2, to);
		query.setParameter(3, firstDayMonthDate);
		query.setParameter(4, lastDayMonthDate);
		List<Date> dates = query.getResultList();
	 	 for (Date d:dates){
		   res.add(d);
		  }
	 	return res;
	}


public void open(){
		
		String fileName=c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);
			  db = emf.createEntityManager();
    	   }
		System.out.println("DataAccess opened => isDatabaseLocal: "+c.isDatabaseLocal());

		
	}

	public void close(){
		db.close();
		System.out.println("DataAcess closed");
	}
	
	public User login(String log, String pass) {
	    try {
	        open(); // Aseg�rate de que open() es la apertura de una conexi�n a la base de datos
	        // Usar par�metros con nombre para mayor claridad
	        TypedQuery<User> query = db.createQuery(
	                "SELECT u FROM User u WHERE u.log = :log AND u.password = :pass", User.class);
	        query.setParameter("log", log);
	        query.setParameter("pass", pass);
	        
	        return query.getSingleResult();
	    } catch (NoResultException e) {
	        System.out.println("Ez da aurkitu erabiltzaile edo kontrase�a hori duen erabiltzailerik");
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Errore bat gertatu da");
	    } finally {
	        close(); // Aseg�rate de que close() cierra la conexi�n correctamente
	    }
	    return null; // Retorna null si no se encuentra el usuario o si ocurre alg�n error
	}
	public void register(String mota, String NAN,String log, String password, String email, String izena,String abizena, String jaiotzeData, int telefonoZenbakia, String sexua) {
		db.getTransaction().begin();
		try {
			User user;
			if(mota.equals("Gidari")) {
				user=new Driver(NAN, log, password, email, izena, abizena, jaiotzeData, telefonoZenbakia, sexua);
			}else {
				user=new Traveler(NAN, log, password, email, izena, abizena, jaiotzeData, telefonoZenbakia, sexua);
			}
			db.persist(user);
			db.getTransaction().commit();
		}catch (Exception e){
				e.printStackTrace();
		}
	}
	public boolean existitu(String NAN, String email, String log, int telefonoZenbakia) {
	    try {
	        open(); // Abrimos la conexi�n con la base de datos

	        TypedQuery<User> query = db.createQuery(
	            "SELECT u FROM User u WHERE u.NAN = :NAN OR u.email = :email OR u.log = :log OR u.telefonoZenbakia = :telefonoZenbakia",
	            User.class
	        );

	        // Asignamos los par�metros a la consulta
	        query.setParameter("NAN", NAN);
	        query.setParameter("email", email);
	        query.setParameter("log", log);
	        query.setParameter("telefonoZenbakia", telefonoZenbakia);

	        query.getSingleResult(); // Si hay resultado, el usuario ya existe
	        return true; 
	    } catch (NoResultException e) {
	        return false; // No se encontr� un usuario con estos datos
	    } catch (Exception e) {
	        System.err.println("Error verificando la existencia del usuario: " + e.getMessage());
	        return false; 
	    } finally {
	        close(); // Cerramos la conexi�n con la base de datos
	    }
	}
	
	//galdetu metodoa ondo dagoen
	
	public void diruaSartu(float kant, String NAN){
	    try {
	    	db.getTransaction().begin();
	    	User u=db.find(User.class, NAN);
	        float d=u.diruaSartu(kant);
	        db.persist(u);
	        db.getTransaction().commit();
	    } catch (Exception e) {
	        db.getTransaction().rollback(); 
	        e.printStackTrace();
	    } 
	}
	//public void diruaAtera(float kant, String NAN
	public Book createBook(String NAN, Integer rideNumber, int seats)throws bookAlreadyExistException, NoCashException,NotEnoughSeatsException {
		try {
			db.getTransaction().begin();
			Traveler t=db.find(Traveler.class,NAN);
			Ride r=db.find(Ride.class, rideNumber);
			
			if(t.existBook(r)) {
				db.getTransaction().commit();
				throw new bookAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.BookAlreadyExist"));
			}
			
			if(r.getnPlaces()<seats) {
				db.getTransaction().commit();
				throw new NotEnoughSeatsException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.NotEnoughSeats"));
			}
			if(!t.hasCash(r, seats)) {
				db.getTransaction().commit();
				throw new NoCashException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.NoCash"));
			}
			//float prezioa=r.getPrice()*seats;
			float price=r.calculatePrice(seats);	
			Book book=t.addBook(r, seats);
			book.setDiruIzoztuta(price);
			float d= t.diruaAtera(price);
			//t.setDirua(t.getDirua()-prezioa);
			//db.persist(book);
			r.addBook(book);
			//r.setnPlaces((int)r.getnPlaces() - seats);
			int nPlaces=r.updatenPlaces(seats);
			db.persist(t);
			db.getTransaction().commit();
			return book;
		}catch (NullPointerException e){
			db.getTransaction().commit();
			return null;
		}
	}
	/**
	private void updateMoney(Traveler t, float kant) {
		try {
			db.getTransaction().begin();
	        t.setDirua(t.getDirua() -kant); 

	        String jpql = "UPDATE Traveler t SET t.dirua = :dirua WHERE t.NAN = :NAN";
	        Query query = db.createQuery(jpql);

	        query.setParameter("dirua", t.getDirua()); 
	        query.setParameter("NAN", t.getNAN()); 
	        
	        db.merge(t);
	        db.getTransaction().commit();
	    } catch (Exception e) {
	        db.getTransaction().rollback(); 
	        e.printStackTrace();
	    } 
	}
	*/
	public void denyBook(Integer bookNumber, Integer rideNumber, String NAN) {
		try {
			db.getTransaction().begin();
			Book b=db.find(Book.class, bookNumber);
			Traveler t=db.find(Traveler.class, NAN);
			Ride r=db.find(Ride.class, rideNumber);
			t.setDirua(b.getDiruIzoztuta()+t.getDirua());
			LocalDateTime dataOrdua=LocalDateTime.now();
	        DateTimeFormatter formatoa=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	        String dataOrduaFormateatuta=dataOrdua.format(formatoa);
	        t.addMovement(dataOrduaFormateatuta, b.getDiruIzoztuta(), ResourceBundle.getBundle("Etiquetas").getString("Mugimendua.diruaSartuSaldora"));
			t.getBookListb().remove(b);
			r.getBookList().remove(b);
			r.setnPlaces((int)r.getnPlaces()+b.getEserlekuKop());
			db.persist(t);
			db.remove(b);
			db.getTransaction().commit();
		}catch(Exception e) {
			db.getTransaction().rollback();
			e.printStackTrace();
		}
	}
	public List<Book>getBooks(String NAN){
		List<Book> book = new ArrayList<>();	
		TypedQuery<Book> query=db.createQuery("SELECT b FROM Book b WHERE b.traveler.NAN=:nan", Book.class);
		query.setParameter("nan",NAN );
		List<Book>books=query.getResultList();
		 for (Book b:books){
			   book.add(b);
			  }
		 	return book;
	}
	/**
	 * try {
			db.getTransaction().begin();
			Traveler t=db.find(Traveler.class,NAN);
			Ride r=db.find(Ride.class, rideNumber);
			
			if(t.existBook(r)) {
				db.getTransaction().commit();
				throw new bookAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.BookAlreadyExist"));
			}
			
			if(r.getnPlaces()<seats) {
				db.getTransaction().commit();
				throw new NotEnoughSeatsException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.NotEnoughSeats"));
			}
			if(!t.hasCash(r, seats)) {
				db.getTransaction().commit();
				throw new NoCashException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.NoCash"));
			}
			Book book=t.addBook(r, seats);
			//db.persist(book);
			r.addBook(book);
			r.setnPlaces((int)r.getnPlaces() - seats);
			
			db.persist(t);
			
			db.getTransaction().commit();
			return book;
		}catch (NullPointerException e){
			db.getTransaction().commit();
			return null;
		}
	 *
	 */
	public Kotxe createCar(String marka, String modeloa, int eserlekuKopurua, String matrikula, String NAN) throws carAlreadyExistsException  {
		try {
			db.getTransaction().begin();
			Driver d=db.find(Driver.class, NAN);
			if(d.existCar(matrikula)) {
				db.getTransaction().commit();
				throw new carAlreadyExistsException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.carAlreadyExistsException"));
			}
			Kotxe kotxe=d.addCar(marka, modeloa, eserlekuKopurua, matrikula);
			db.persist(d);
			db.getTransaction().commit();
			return kotxe;
		}catch(NullPointerException e){
			db.getTransaction().commit();
			return null;
		}
    }
	public List<Kotxe> getCars(String NAN){
		List<Kotxe> kotxeak = new ArrayList<>();	
		TypedQuery<Kotxe> query=db.createQuery("SELECT k FROM Kotxe k WHERE k.driver.NAN=:nan", Kotxe.class);
		query.setParameter("nan",NAN );
		List<Kotxe>kotxeakk=query.getResultList();
		 for (Kotxe k:kotxeakk){
			   kotxeak.add(k);
			  }
		 	return kotxeak;
	}
	public List<Mugimendua> getMugimenduak(String NAN){
		List<Mugimendua> mugimenduak = new ArrayList<>();	
		TypedQuery<Mugimendua> query=db.createQuery("SELECT m FROM Mugimendua m WHERE m.user.NAN=:nan", Mugimendua.class);
		query.setParameter("nan",NAN );
		List<Mugimendua>mugimenduakk=query.getResultList();	
		 for (Mugimendua m:mugimenduakk){
			   mugimenduak.add(m);
			  }
		 	return mugimenduak;
	}
	public List<Balorazio> getBalorazioak(String NAN){
		Traveler t=db.find(Traveler.class, NAN);
		List<Balorazio> balorazioak=t.getBalorazioak();
		return balorazioak;
	}
	public List<Balorazio> getBalorazioakFromRide(Integer rideNumber){
		Ride r=db.find(Ride.class, rideNumber);
		List<Balorazio> balorazioak=r.getBalorazioak();
		return balorazioak;
	}
	public List<Erreklamazio> getReceivedReports(String NAN){
		User u=db.find(User.class, NAN);
		List<Erreklamazio> jasotakoErreklamazioak=u.getJasotakoErreklamazioak();
		return jasotakoErreklamazioak;
	}
	public List<Erreklamazio> getCreatedReports(String NAN){
		User u=db.find(User.class, NAN);
		List<Erreklamazio> sortutakoErreklamazioak=u.getSortutakoErreklamazioak();
		return sortutakoErreklamazioak;
	}
	public List<Erreklamazio> getReportsFromRide(Integer rideNumber){
		Ride r=db.find(Ride.class, rideNumber);
		List<Erreklamazio> erreklamazioak=r.getErreklamazioak();
		return erreklamazioak;
	}
	public List<Erreklamazio> getReports(){
		List<Erreklamazio> erreklamazioak=new ArrayList<Erreklamazio>();
		TypedQuery<Erreklamazio> query=db.createQuery("SELECT e FROM Erreklamazio e", Erreklamazio.class);
		List<Erreklamazio> erreklamazioakk=query.getResultList();
		for(Erreklamazio e: erreklamazioakk) {
			erreklamazioak.add(e);
		}
		return erreklamazioak;
	}
	/**
	public Mugimendua createMovement(String NAN, String data,float diruKantitatea, String deskribapena) {
		try {
			db.getTransaction().begin();
			User u= db.find(User.class, NAN);
			Mugimendua m=u.addMovement(data, diruKantitatea, deskribapena);
			db.persist(u);
			db.getTransaction().commit();
			return m;
		}catch(NullPointerException e){
			db.getTransaction().commit();
			return null;
		}
	}
	*/
	public float eskuratuDirua(String NAN) {
		float dirua=0;
	
		try {
			db.getTransaction().begin();
			User u= db.find(User.class, NAN);
			dirua=u.getDirua();
			return dirua;
		}catch(NullPointerException e){
			db.getTransaction().commit();
			return dirua;
	}
}
	public void confirmBook(Integer bookNumber, String NAN)throws rideIsNotConfirmed {
		try {
			db.getTransaction().begin();
			Book book=db.find(Book.class, bookNumber);
			//Driver d=db.find(Driver.class, NAN);
			User u=db.find(User.class, NAN);
			if (book.rideConfirmed()==false) {
				db.getTransaction().commit();
				throw new rideIsNotConfirmed(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.rideNotConfirmed"));
			}
			float kant=book.getDiruIzoztuta();
			float d=u.diruaSartu(kant);
			book.setEgoera(ResourceBundle.getBundle("Etiquetas").getString("Book.completed"));
			book.setEgoeraB(true);
			/**
			d.setDirua(book.getDiruIzoztuta()+d.getDirua());
			LocalDateTime dataOrdua=LocalDateTime.now();
	        DateTimeFormatter formatoa=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	        String dataOrduaFormateatuta=dataOrdua.format(formatoa);
	        d.addMovement(dataOrduaFormateatuta, book.getDiruIzoztuta(), ResourceBundle.getBundle("Etiquetas").getString("Mugimendua.diruaSartuSaldora"));
	        */
			book.setDiruIzoztuta(0);
			db.persist(u);
			db.getTransaction().commit();
		}catch(NullPointerException e){
			db.getTransaction().commit();

		}
	}
	//cambiar metodo
	public void cancelRide(Integer rideNumber, String NAN) {
		try {
			db.getTransaction().begin();
			Ride ride=db.find(Ride.class, rideNumber);
			Driver driver=db.find(Driver.class, NAN);
			Iterator<Book>iterator= ride.getBookList().iterator();
			while (iterator.hasNext()) {
				Book book=iterator.next();
				Traveler t=book.getTraveler();
				float kant=book.getDiruIzoztuta();
				float dirua=t.diruaSartu(kant);
				/**
				LocalDateTime dataOrdua=LocalDateTime.now();
		        DateTimeFormatter formatoa=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		        String dataOrduaFormateatuta=dataOrdua.format(formatoa);
		        t.addMovement(dataOrduaFormateatuta, book.getDiruIzoztuta(), ResourceBundle.getBundle("Etiquetas").getString("Mugimendua.diruaSartuSaldora"));
		        */
				t.removeBook(book);
				//db.remove(book);
				//ride.getBookList().remove(book);
				//iterator.remove();
			}
			//ride.getBookList().clear();
			driver.removeRide(ride);
			//db.persist(driver);
			db.remove(ride);
			db.getTransaction().commit();
		}catch(NullPointerException e){
			db.getTransaction().commit();

		}
	}
	public void confirmRide(Integer rideNumber) {
		try {
			db.getTransaction().begin();
			Ride ride=db.find(Ride.class,rideNumber );
			ride.setEginda(true);
			db.getTransaction().commit();
		}catch(NullPointerException e){
			db.getTransaction().commit();

		}
	}
	public void acceptReport(Integer idReport) {
		try {
			db.getTransaction().begin();
			Erreklamazio e=db.find(Erreklamazio.class,idReport);
			e.setEgoera(ResourceBundle.getBundle("Etiquetas").getString("Erreklamazio.accepted"));
			db.getTransaction().commit();
		}catch(NullPointerException e){
			db.getTransaction().commit();

		}
	}
	public void denyReport(Integer idReport) {
		try {
			db.getTransaction().begin();
			Erreklamazio e=db.find(Erreklamazio.class,idReport);
			e.setEgoera(ResourceBundle.getBundle("Etiquetas").getString("Erreklamazio.dennied"));
			db.getTransaction().commit();
		}catch(NullPointerException e){
			db.getTransaction().commit();

		}
	}
	public Alerta createAlert(String nondik, String nora, Date data, String NAN) throws alertAlreadyExists, RideMustBeLaterThanTodayException{
		try {
			if(new Date().compareTo(data)>0) {
				throw new RideMustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.ErrorRideMustBeLaterThanToday"));
			}
			db.getTransaction().begin();
			Traveler t= db.find(Traveler.class,NAN );
			if (t.doesAlertExist(nondik,nora, data)) {
				db.getTransaction().commit();
				throw new alertAlreadyExists(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.alertAlreadyExists"));
			}
			Alerta alerta=t.addAlert(nondik, nora, data);
			//db.persist(t); -> ez da beharrezkoa, dagoeneko commit egiten delako
			db.getTransaction().commit();
			return alerta;
		} catch (NullPointerException e) {
			db.getTransaction().commit();
			return null;
		}
	}
	public List<Ride> getRidesRelatedToAlerts(String NAN){
		try {
			db.getTransaction().begin();
			Traveler t= db.find(Traveler.class,NAN );
			TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r ",Ride.class);
			List <Ride> rides=query.getResultList();
			List<Alerta> alerts=t.getAlertak();
			List<Ride> ridesRelatedToAlerts=new ArrayList<Ride>();
			for(Alerta a:alerts) {
				for(Ride r:rides){
					if(a.alertEqualsRide(r.getFrom(), r.getTo(), r.getDate())) {
						ridesRelatedToAlerts.add(r);
						a.setEginda(true);
					}
				}
			}
			db.getTransaction().commit();
			return ridesRelatedToAlerts ;
		}catch(NullPointerException e) {
			db.getTransaction().commit();
			return null;
		}
			
		}
	public void updateLastLogged(String NAN, String lastLogged) {
		try {
			db.getTransaction().begin();
			Traveler t=db.find(Traveler.class,NAN);
			t.setLastLogged(lastLogged);
			db.getTransaction().commit();
		}catch(NullPointerException e){
			db.getTransaction().commit();

		}
	}
	public Balorazio createBalorazio(Integer idBalorazio, int puntuazioa, String komentarioa, String data, String NAN, Integer rideNumber) throws reviewAlreadyExistsException, ratingMoreThanFiveException{
		try {
			if(NAN==null||rideNumber==null||rideNumber<0) {
				return null;
			}
			db.getTransaction().begin();
			Traveler t= db.find(Traveler.class,NAN );
			Ride r=db.find(Ride.class,rideNumber );
			if(t.balorazioExist(rideNumber)) {
				db.getTransaction().commit();
				throw new reviewAlreadyExistsException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.reviewAlreadyExistsException"));
			}
			if(puntuazioa>5) {
				db.getTransaction().commit();
				throw new ratingMoreThanFiveException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ratingMoreThanFiveException"));
			}
			Balorazio b=t.addBalorazio(idBalorazio, puntuazioa, komentarioa, data, r);
			r.addBalorazio(b);
			db.getTransaction().commit();
			return b;
		}catch(NullPointerException e){
			db.getTransaction().commit();
			return null;
		}
	}
	public Erreklamazio createErreklamazio (String deskribapena, String data, String erreklamatzaileNAN, String erreklamatuNAN, Integer rideNumber) throws erreklamazioAlreadyExistsException {
		try {
			db.getTransaction().begin();
			User erreklamatzaile=db.find(User.class, erreklamatzaileNAN);
			User erreklamatua=db.find(User.class, erreklamatuNAN);
			Ride r=db.find(Ride.class, rideNumber);
			if(erreklamatzaile.erreklamazioExist(rideNumber)) {
				db.getTransaction().commit();
				throw new erreklamazioAlreadyExistsException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.erreklamazioAlreadyExistsException"));
			}
			Erreklamazio erreklamazio=erreklamatzaile.addSortutakoErreklamazio(deskribapena, data, erreklamatua, r);
			erreklamatua.addJasotakoErreklamazio(erreklamazio);
			r.addErreklamazio(erreklamazio);
			db.getTransaction().commit();
			return erreklamazio;
		}catch(NullPointerException e){
			db.getTransaction().commit();
			return null;
		}
	}
	public void deleteUser(String NAN) {
		try {
			db.getTransaction().begin();
			User u=db.find(User.class, NAN);
			if (u instanceof Driver) {
				Driver d=(Driver) u;
				d.removeBidaiak();
				d.removeBidalitakoErreklamazioak();
				d.removeJasotakoErreklamazioak();
				d.removeBalorazioak();
				db.remove(d);
			}else {
				Traveler t=(Traveler) u;
				t.removeBidalitakoErreklamazioak();
				t.removeJasotakoErreklamazioak();
				//t.removeMugimenduak();
				t.removeBooks();
				t.removeBalorazioak();
				//t.removeAlerts();
				db.remove(t);
			}
			db.getTransaction().commit();
		}catch(NullPointerException e){
			db.getTransaction().commit();
		}
	}
	
	public void removeBalorazio(Integer idBalorazioa) {
	    try {
	        db.getTransaction().begin();
	        Balorazio b = db.find(Balorazio.class, idBalorazioa);

	        if (b != null) {
	            Ride r = b.getRide();
	            if (r != null) {
	                r.getBalorazioak().remove(b);
	            }
	            Traveler t = b.getTraveler();
	            if (t != null) {
	                t.getBalorazioak().remove(b);
	            }
	            db.remove(b);
	        }

	        db.getTransaction().commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        if (db.getTransaction().isActive()) {
	            db.getTransaction().rollback();
	        }
	    }
	}


}
