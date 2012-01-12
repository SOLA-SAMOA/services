/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.cadastre.repository.entities;

import java.util.List;
import javax.persistence.Column;
import org.sola.services.common.repository.AccessFunctions;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;

/**
 *
 * @author Elton Manoku
 */
public class CadastreObjectNode extends AbstractReadOnlyEntity{

    public static String QUERY_GET_BY_RECTANGLE_FROM_PART = 
            "st_dumppoints((select co.geom_polygon from cadastre.cadastre_object co "
            + " where type_code= 'parcel' and status_code= 'current' "
            + " and ST_Intersects(co.geom_polygon, SetSRID("
            + "ST_MakeBox3D(ST_Point(#{minx}, #{miny}),ST_Point(#{maxx}, #{maxy})), #{srid})) "
            + "limit 1)) t  ";
    
    public static String QUERY_GET_BY_RECTANGLE_WHERE_PART = 
            " ST_Intersects(t.geom, SetSRID("
            + "ST_MakeBox3D(ST_Point(#{minx}, #{miny}),ST_Point(#{maxx}, #{maxy})), #{srid})) ";
    
    @Column(name = "id")
    @AccessFunctions(onSelect = "cast(uuid_generate_v1() as varchar)")
    private String id;
    @Column(name = "geom")
    @AccessFunctions(onSelect = "st_asewkb(geom)")
    private byte[] geom;
    private List<CadastreObject> cadastreObjectList;

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geom) {
        this.geom = geom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CadastreObject> getCadastreObjectList() {
        return cadastreObjectList;
    }

    public void setCadastreObjectList(List<CadastreObject> cadastreObjectList) {
        this.cadastreObjectList = cadastreObjectList;
    }
    
}
