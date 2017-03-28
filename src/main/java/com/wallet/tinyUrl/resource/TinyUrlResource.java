package com.wallet.tinyUrl.resource;

import com.codahale.metrics.annotation.Timed;
import com.wallet.tinyUrl.core.TinyUrl;
import com.wallet.tinyUrl.dao.TinyUrlDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Created by zxqiu on 3/27/17.
 */

@Path("/t")
public class TinyUrlResource {
    private static final Logger logger_ = LoggerFactory.getLogger(TinyUrlResource.class);

    private TinyUrlDAOConnector tinyUrlDAOC = null;

    public TinyUrlResource() throws Exception {
        this.tinyUrlDAOC = TinyUrlDAOConnector.instance();
    }

    @GET
    @Timed
    @Path("/getall")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TinyUrl> getAll() {
        return tinyUrlDAOC.getAll();
    }

    @POST
    @Timed
    @Path("/toshort")
    @Produces(MediaType.APPLICATION_JSON)
    public TinyUrl toShort(
            String request
            ) throws Exception {
        JSONObject tinyUrlParam = new JSONObject(request);
        String full_url = tinyUrlParam.getString(Dict.FULL_URL);
        int expire_click = tinyUrlParam.getInt(Dict.EXPIRE_CLICK);

        List<TinyUrl> tinyUrls = tinyUrlDAOC.getByFullUrl(full_url);
        if (!tinyUrls.isEmpty()) {
            return tinyUrls.get(tinyUrls.size() - 1);
        }

        TinyUrl tinyUrl = new TinyUrl(full_url, expire_click);

        try {
            tinyUrlDAOC.insert(tinyUrl);
        } catch (Exception e) {
            // retry to solve duplicate generated short url issue
            tinyUrl = new TinyUrl(full_url, expire_click);
            tinyUrlDAOC.insert(tinyUrl);
        }

        return tinyUrl;
    }

    @GET
    @Timed
    @Path("/tolong/{" + Dict.SHORT_URL + "}")
    @Produces(MediaType.TEXT_HTML)
    public Response toLong(
            @PathParam(Dict.SHORT_URL) String short_url
            ){
        List<TinyUrl> tinyUrls = tinyUrlDAOC.getByShortUrl(short_url);
        if (tinyUrls.isEmpty()) {
            return Response.status(400).entity(ApiUtils.buildJSONResponse(false, "short url not exists")).build();
        }

        TinyUrl tinyUrl = tinyUrls.get(tinyUrls.size() - 1);
        if (tinyUrl.getExpire_click() == 1) {
            // last one click is consumed
            tinyUrlDAOC.deleteByShortUrl(short_url);
        } else if (tinyUrl.getExpire_click() > 1) {
            tinyUrl.setExpire_click(tinyUrl.getExpire_click() - 1);
            tinyUrlDAOC.updateExpireClickByShortUrl(tinyUrl);
        }
        // if expire_click is 0, it means infinite

        String full_url = tinyUrl.getFull_url();

        return Response.seeOther(URI.create(full_url)).build();
    }
}
