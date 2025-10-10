package tests;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import dataAccess.DataAccess;
import domain.Balorazio;
import domain.Driver;
import domain.Kotxe;
import domain.Ride;
import domain.Traveler;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.ratingMoreThanFiveException;
import exceptions.reviewAlreadyExistsException;
import testOperations.TestDataAccess;

public class createBalorazioWhiteTestBD {

	static DataAccess sut=new DataAccess();
	static TestDataAccess testDA=new TestDataAccess();
  
    @Test
    public void test1() {
        //Balorazio
        Integer idBalorazioa = 345;
        int puntuazioa = 5;
        String komentarioa = "Ondo";
        String data = "02/01/2025";

        // Traveler
        String travelerNAN = "73456543M";

        // Ride
        Integer rideNumber = 12;


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = null;
        try {
            rideDate = sdf.parse("05/06/2025");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Balorazio b = null;

        try {
            sut.open();
            b = sut.createBalorazio(idBalorazioa, puntuazioa, komentarioa, data, travelerNAN, rideNumber);
            sut.close();

            assertNotNull(b);

            testDA.open();
            boolean exist = testDA.existBalorazio(idBalorazioa, puntuazioa, komentarioa, data, travelerNAN, rideNumber);
            assertTrue(exist);
            testDA.close();
            
            fail();
        } catch (reviewAlreadyExistsException e) {
        	fail();
        } catch (ratingMoreThanFiveException e) {
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void test2() {
        //Balorazio
        Integer idBalorazioa = 345;
        int puntuazioa = 5;
        String komentarioa = "Ondo";
        String data = "02/01/2025";

        // Traveler
        String travelerNAN = "73456543M";
        String travelerLog = "traveler1";
        String travelerPass = "123";
        String travelerEmail = "traveler1@gmail.com";
        String travelerIzena = "Ibai";
        String travelerAbizena = "Martin";
        String travelerJaiotze = "01/07/1997";
        int travelerTelefono = 612332456;
        String travelerSexu = "male";

        // Driver
        String driverNAN = "99887765L";
        String driverLog = "driver1";
        String driverPass = "123";
        String driverEmail = "driver1@gmail.com";
        String driverIzena = "Jon";
        String driverAbizena = "Arrieta";
        String driverJaiotze = "10/11/1985";
        int driverTelefono = 634567890;
        String driverSexu = "male";

        // Ride
        Integer rideNumber = 12;
        String from = "Donostia";
        String to = "Bilbo";
        int nPlaces = 4;
        float price = 30.00f;

        // Kotxe
        String matrikula = "1234ABC";
        String marka = "Toyota";
        String modeloa = "Corolla";
        int eserleku_kop = 5;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = null;
        try {
            rideDate = sdf.parse("05/06/2025");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Balorazio b = null;
        boolean exist = false;

        try {
            testDA.open();
            Driver d = testDA.createDriver(driverNAN, driverLog, driverPass, driverEmail, driverIzena, driverAbizena, driverJaiotze, driverTelefono, driverSexu);
            Kotxe k = testDA.createKotxe(matrikula, marka, modeloa, eserleku_kop, d);
            Traveler traveler = testDA.createTraveler(travelerNAN, travelerLog, travelerPass, travelerEmail, travelerIzena, travelerAbizena, travelerJaiotze, travelerTelefono, travelerSexu);
            Ride ride = testDA.createRide(rideNumber, from, to, rideDate, nPlaces, price, d, k);
            testDA.addRideNumberWithBalorazio(idBalorazioa, puntuazioa, komentarioa, data, rideNumber, travelerNAN);
            testDA.close();

            sut.open();
            b = sut.createBalorazio(idBalorazioa, puntuazioa, komentarioa, data, travelerNAN, rideNumber);
            sut.close();

            assertNull(b);

            testDA.open();
            exist = testDA.existBalorazio(idBalorazioa, puntuazioa, komentarioa, data, travelerNAN, rideNumber);
            assertTrue(!exist);
            testDA.close();
            
        } catch (reviewAlreadyExistsException e) {
        	assertTrue(true);
        } catch (ratingMoreThanFiveException e) {
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            testDA.open();
            testDA.removeTraveler(travelerNAN);
            testDA.removeRide(rideNumber);
            testDA.removeDriver(driverNAN);
            testDA.removeCarFromDB(matrikula);
            testDA.close();
        }
    }
    
    @Test
    public void test3() {
        //Balorazio
        Integer idBalorazioa = 345;
        int puntuazioa = 8;
        String komentarioa = "Ondo";
        String data = "02/01/2025";

        // Traveler
        String travelerNAN = "73456543M";
        String travelerLog = "traveler1";
        String travelerPass = "123";
        String travelerEmail = "traveler1@gmail.com";
        String travelerIzena = "Ibai";
        String travelerAbizena = "Martin";
        String travelerJaiotze = "01/07/1997";
        int travelerTelefono = 612332456;
        String travelerSexu = "male";

        // Driver
        String driverNAN = "99887765L";
        String driverLog = "driver1";
        String driverPass = "123";
        String driverEmail = "driver1@gmail.com";
        String driverIzena = "Jon";
        String driverAbizena = "Arrieta";
        String driverJaiotze = "10/11/1985";
        int driverTelefono = 634567890;
        String driverSexu = "male";

        // Ride
        Integer rideNumber = 12;
        String from = "Donostia";
        String to = "Bilbo";
        int nPlaces = 4;
        float price = 30.00f;

        // Kotxe
        String matrikula = "1234ABC";
        String marka = "Toyota";
        String modeloa = "Corolla";
        int eserleku_kop = 5;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = null;
        try {
            rideDate = sdf.parse("05/06/2025");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Balorazio b = null;
        boolean exist = false;

        try {
            testDA.open();
            Driver d = testDA.createDriver(driverNAN, driverLog, driverPass, driverEmail, driverIzena, driverAbizena, driverJaiotze, driverTelefono, driverSexu);
            Kotxe k = testDA.createKotxe(matrikula, marka, modeloa, eserleku_kop, d);
            Traveler traveler = testDA.createTraveler(travelerNAN, travelerLog, travelerPass, travelerEmail, travelerIzena, travelerAbizena, travelerJaiotze, travelerTelefono, travelerSexu);
            Ride ride = testDA.createRide(rideNumber, from, to, rideDate, nPlaces, price, d, k);
            testDA.close();

            sut.open();
            b = sut.createBalorazio(idBalorazioa, puntuazioa, komentarioa, data, travelerNAN, rideNumber);
            sut.close();

            assertNotNull(b);

            testDA.open();
            exist = testDA.existBalorazio(idBalorazioa, puntuazioa, komentarioa, data, travelerNAN, rideNumber);
            assertTrue(exist);
            testDA.close();
            
            fail();
        } catch (reviewAlreadyExistsException e) {
        	fail();
        } catch (ratingMoreThanFiveException e) {
        	assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            testDA.open();
            testDA.removeTraveler(travelerNAN);
            testDA.removeRide(rideNumber);
            testDA.removeDriver(driverNAN);
            testDA.removeCarFromDB(matrikula);
            testDA.close();
        }
    }
    
    @Test
    public void test4() {
        //Balorazio
        Integer idBalorazioa = 345;
        int puntuazioa = 5;
        String komentarioa = "Ondo";
        String data = "02/01/2025";

        // Traveler
        String travelerNAN = "73456543M";
        String travelerLog = "traveler1";
        String travelerPass = "123";
        String travelerEmail = "traveler1@gmail.com";
        String travelerIzena = "Ibai";
        String travelerAbizena = "Martin";
        String travelerJaiotze = "01/07/1997";
        int travelerTelefono = 612332456;
        String travelerSexu = "male";

        // Driver
        String driverNAN = "99887765L";
        String driverLog = "driver1";
        String driverPass = "123";
        String driverEmail = "driver1@gmail.com";
        String driverIzena = "Jon";
        String driverAbizena = "Arrieta";
        String driverJaiotze = "10/11/1985";
        int driverTelefono = 634567890;
        String driverSexu = "male";

        // Ride
        Integer rideNumber = 12;
        String from = "Donostia";
        String to = "Bilbo";
        int nPlaces = 4;
        float price = 30.00f;

        // Kotxe
        String matrikula = "1234ABC";
        String marka = "Toyota";
        String modeloa = "Corolla";
        int eserleku_kop = 5;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = null;
        try {
            rideDate = sdf.parse("05/06/2025");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Balorazio b = null;

        try {
            testDA.open();
            Driver d = testDA.createDriver(driverNAN, driverLog, driverPass, driverEmail, driverIzena, driverAbizena, driverJaiotze, driverTelefono, driverSexu);
            Kotxe k = testDA.createKotxe(matrikula, marka, modeloa, eserleku_kop, d);
            Traveler traveler = testDA.createTraveler(travelerNAN, travelerLog, travelerPass, travelerEmail, travelerIzena, travelerAbizena, travelerJaiotze, travelerTelefono, travelerSexu);
            Ride ride = testDA.createRide(rideNumber, from, to, rideDate, nPlaces, price, d, k);
            testDA.close();

            sut.open();
            b = sut.createBalorazio(idBalorazioa, puntuazioa, komentarioa, data, travelerNAN, rideNumber);
            sut.close();

            assertNotNull(b);

            testDA.open();
            boolean exist = testDA.existBalorazio(idBalorazioa, puntuazioa, komentarioa, data, travelerNAN, rideNumber);
            assertTrue(exist);
            testDA.close();

        } catch (reviewAlreadyExistsException e) {
        	fail();
        } catch (ratingMoreThanFiveException e) {
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
        	sut.open();
            sut.removeBalorazio(b.getId());
            sut.close();
            testDA.open();
            testDA.removeRide(rideNumber);
            testDA.removeTraveler(travelerNAN);
            testDA.removeDriver(driverNAN);
            testDA.removeCarFromDB(matrikula);
            testDA.close();
        }
    }
}