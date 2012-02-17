/*
 *  Food and Agriculture Orgainsation (FAO) of the United Nations
 *  Solutions for Open Land Administration - Sola.
 * 
 * 
 *  Copyright 2011, FAO and UN, and individual contributors as indicated
 *  by the @authors. See the copyright text in the distribution for a
 *  full listing of individual contributors.
 * 
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 * 
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.sola.services.ejb.application.repository.entities;

import java.math.BigDecimal;
import javax.persistence.Column;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;


/**
 *
 * @author RizzoM
 */

//@Table(name = "service_historic", schema = "application")

public class LodgementView extends AbstractReadOnlyEntity {

    public static final String PARAMETER_FROM = "fromDate";
    public static final String PARAMETER_TO = "toDate";
    public static final String QUERY_GETLODGEMENT = "select * from application.getlodgement(#{" + 
            PARAMETER_FROM + "}, #{" + PARAMETER_TO + "}) "
            + " as LodgementReport(resultType  varchar, resultGroup varchar, resultTotal integer, "
            + "resultTotalPerc decimal, resultDailyAvg  decimal, resultTotalReq integer, "
            + "resultReqPerc  decimal) ";

    @Column(name="resultType")
    private String  resultType;
    @Column(name="resultGroup")
    private String  resultGroup;
    @Column(name="resultTotal")
    private Integer resultTotal;
    @Column(name="resultTotalPerc")
    private BigDecimal  resultTotalPerc;
    @Column(name="resultDailyAvg")
    private BigDecimal  resultDailyAvg;
    @Column(name="resultTotalReq")
    private Integer resultTotalReq;
    @Column(name="resultReqPerc")
    private BigDecimal  resultReqPerc;
    

    public LodgementView() {
        super();
    }

    public BigDecimal getResultDailyAvg() {
        return resultDailyAvg;
    }

    public void setResultDailyAvg(BigDecimal resultDailyAvg) {
        this.resultDailyAvg = resultDailyAvg;
    }

    public String getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(String resultGroup) {
        this.resultGroup = resultGroup;
    }

    public BigDecimal getResultReqPerc() {
        return resultReqPerc;
    }

    public void setResultReqPerc(BigDecimal resultReqPerc) {
        this.resultReqPerc = resultReqPerc;
    }

    public Integer getResultTotal() {
        return resultTotal;
    }

    public void setResultTotal(Integer resultTotal) {
        this.resultTotal = resultTotal;
    }

    public BigDecimal getResultTotalPerc() {
        return resultTotalPerc;
    }

    public void setResultTotalPerc(BigDecimal resultTotalPerc) {
        this.resultTotalPerc = resultTotalPerc;
    }

    public Integer getResultTotalReq() {
        return resultTotalReq;
    }

    public void setResultTotalReq(Integer resultTotalReq) {
        this.resultTotalReq = resultTotalReq;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
    
}
