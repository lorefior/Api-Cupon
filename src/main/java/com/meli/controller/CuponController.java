package com.meli.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meli.model.CuponRequest;
import com.meli.model.CuponResponse;
import com.meli.service.impl.CuponServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

@RestController
@Api(value="Api-Coupon")
public class CuponController {
	@Autowired
	CuponServiceImpl service;
    
	
	@PostMapping(value="coupon")
	@ApiOperation(value="Servicio que retorna la mejor oferta para el Cupon de Beneficio Meli")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operación exitosa"),
			@ApiResponse(code = 404, message = "No encontró oferta posible") 
	})
	public ResponseEntity<CuponResponse> mejorOfertaCoupon(@RequestBody() CuponRequest request) {
		CuponResponse response = new CuponResponse();
		response = service.mejorOfertaCoupon(request);
		if(response == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<CuponResponse>(response, HttpStatus.OK);
	}
}

