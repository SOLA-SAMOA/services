/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
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
