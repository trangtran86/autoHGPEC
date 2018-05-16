package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Java class example
 * The class illustrates how to write comments used
 * to generate JavaDoc documentation
 *
 * @author HaTrang
 * @version 1.0
 */

@Api(tags = "Apps: autoHGPEC")
@Path("/hgpec/v1/")
public interface HGPECresource {
	/**
    *
    * Simple method.
    *
    * The function expose the list of the disease in relation with associated genes
    *
    * @param diseaseName String name of disease
    * @see DiseaseFilter
    * @since version 1.0
    */
	
	//------- select disease ----------
	@ApiOperation(value = "Select disease", notes = "Select disease from Heterogeneous network.\n\n")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Found disease", response = DiseaseFilter.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "Not found Disease", response = ErrorMessage.class)

	})
	@Path("selectDisease/{diseaseName}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DiseaseFilter> getDisease(@ApiParam(value = "Disease name", required = true) @PathParam("diseaseName") String diseaseName);
	
	/**
    *
    * Simple method.
    *
    * The function lists the limit rank top of diseases. All the steps in command must be done.
    *
    * @param diseaseName String name of disease
    * @see DiseaseFilter
    * @since version 1.0
    */
	
	//------- select disease ----------
	@ApiOperation(value = "Return rank top of disease", notes = "Return rank top of disease of users' interest.\n\n")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucess", response = RankedDisease.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "Unsucess. Complete all the steps in CyREST command for result", response = ErrorMessage.class)

	})
	@Path("getRank/Disease/{limit}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<RankedDisease> getRankedDiseases(@ApiParam(value = "Select top number:", required = true) @PathParam("limit") int limit);
	
	/**
    *
    * Simple method.
    *
    * The function lists the limit rank top of genes. All the steps in command must be done.
    *
    * @param diseaseName String name of disease
    * @see DiseaseFilter
    * @since version 1.0
    */
	
	//------- select disease ----------
	@ApiOperation(value = "Return rank top of genes", notes = "Return rank top of disease of users' interest.\n\n")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucess", response = RankedGene.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "Unsucess. Complete all the steps in CyREST command for result", response = ErrorMessage.class)

	})
	@Path("getRank/Genes/{limit}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<RankedGene> getRankedGenes(@ApiParam(value = "Select top number:", required = true) @PathParam("limit") int limit);

}
