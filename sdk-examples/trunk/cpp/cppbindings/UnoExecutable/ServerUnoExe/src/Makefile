#**************************************************************
#
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
#
#**************************************************************

PRJ=../../../../../..
SETTINGS=$(PRJ)/settings

include $(SETTINGS)/settings.mk
include $(SETTINGS)/std.mk
include $(SETTINGS)/dk.mk


IMPLEMENTATION_NAME=com.sun.star.comp.product.example.ClientUnoExeSample
SERVICE_NAME=com.sun.star.bridge.example.ClientUnoExeSample
CONNECTION_URL=uno:socket,host=localhost,port=2083;urp;TextInputStream
EXPORTED_COMPONENT=com.sun.star.io.TextInputStream

# Define non-platform/compiler specific settings
COMP_NAME=ClientUnoExe
COMP_IMPL_NAME=$(COMP_NAME).uno.$(SHAREDLIB_EXT)
COMP_RDB_NAME = $(COMP_NAME).uno.rdb

#this should be in odk/settings/std.mk
OUT_DEPLOY=$(OUT)/deploy

OUT_COMP_INC=$(OUT_INC)/$(COMP_NAME)
OUT_COMP_GEN=$(OUT_MISC)/$(COMP_NAME)
OUT_COMP_SLO=$(OUT_SLO)/$(COMP_NAME)
OUT_COMP_LIB=$(SHAREDLIB_OUT)/$(COMP_NAME)
OUT_COMP_BIN=$(OUT_BIN)/$(COMP_NAME)
OUT_COMP_DEPLOY=$(OUT_DEPLOY)/$(COMP_NAME)

COMP_IMPL=$(OUT_COMP_LIB)/$(COMP_IMPL_NAME)
COMP_RDB = $(OUT_COMP_BIN)/$(COMP_RDB_NAME)
COMP_SERVICES=$(OUT_COMP_LIB)/$(COMP_NAME).rdb

ENV_COMP_SERVICES=-env:URE_MORE_SERVICES=$(URLPREFIX)$(COMP_SERVICES)

CXXFILES = \
	client.cxx \
	components.cxx

SLOFILES = $(patsubst %.cxx,$(OUT_COMP_SLO)/%.$(OBJ_EXT),$(CXXFILES))

# Targets
.PHONY: ALL
ALL : \
	$(COMP_NAME)

include $(SETTINGS)/stdtarget.mk

ifneq "$(DEBUG)" ""
	CC_DEFINES+=-DOSL_DEBUG_LEVEL=3
endif
CC_DEFINES+=\
	-DIMPLEMENTATION_NAME=\"$(IMPLEMENTATION_NAME)\" \
	-DSERVICE_NAME=\"$(SERVICE_NAME)\"


$(OUT_COMP_SLO)/%.$(OBJ_EXT) : %.cxx $(SDKTYPEFLAG)
	@echo
	-$(MKDIR) $(subst /,$(PS),$(@D))
	$(CC) $(CC_FLAGS) $(CC_INCLUDES) -I$(OUT_COMP_INC) $(CC_DEFINES) $(CC_OUTPUT_SWITCH)$(subst /,$(PS),$@) $<
	@echo

ifeq "$(OS)" "WIN"
$(OUT_COMP_LIB)/%.$(SHAREDLIB_EXT) : $(SLOFILES)
	@echo
	-$(MKDIR) $(subst /,$(PS),$(@D))
	-$(MKDIR) $(subst /,$(PS),$(OUT_COMP_GEN))
	$(LINK) $(COMP_LINK_FLAGS) /OUT:$@ \
	/MAP:$(OUT_COMP_GEN)/$(subst $(SHAREDLIB_EXT),map,$(@F)) $(SLOFILES) \
	$(CPPUHELPERLIB) $(CPPULIB) $(SALLIB) $(STLPORTLIB) msvcrt.lib kernel32.lib
	$(LINK_MANIFEST)
	@echo
else
$(OUT_COMP_LIB)/%.$(SHAREDLIB_EXT) : $(SLOFILES)
	@echo
	-$(MKDIR) $(subst /,$(PS),$(@D))
	$(LINK) $(COMP_LINK_FLAGS) $(LINK_LIBS) -o $@ $(SLOFILES) \
	$(CPPUHELPERLIB) $(CPPULIB) $(SALLIB) $(STLPORTLIB) $(STC++LIB) $(CPPUHELPERDYLIB) $(CPPUDYLIB) $(SALDYLIB)
ifeq "$(OS)" "MACOSX"
	$(INSTALL_NAME_URELIBS)  $@
endif
	@echo
endif

$(COMP_SERVICES) :
	@echo
	-$(MKDIR) $(subst /,$(PS),$(@D))
	@echo $(OSEP)?xml version="$(QM)1.0$(QM)" encoding="$(QM)UTF-8$(QM)"?$(CSEP) > $@
	@echo $(OSEP)components xmlns="$(QM)http://openoffice.org/2010/uno-components$(QM)"$(CSEP) >> $@
	@echo $(SQM)  $(SQM)$(OSEP)component loader="$(QM)com.sun.star.loader.SharedLibrary$(QM)" uri="$(QM)$(COMP_IMPL_NAME)$(QM)"$(CSEP) >> $@
	@echo $(SQM)    $(SQM)$(OSEP)implementation name="$(QM)$(IMPLEMENTATION_NAME)$(QM)"$(CSEP) >> $@
	@echo $(SQM)      $(SQM)$(OSEP)service name="$(QM)$(SERVICE_NAME)$(QM)"/$(CSEP) >> $@
	@echo $(SQM)    $(SQM)$(OSEP)/implementation$(CSEP) >> $@
	@echo $(SQM)  $(SQM)$(OSEP)/component$(CSEP) >> $@
	@echo $(OSEP)/components$(CSEP) >> $@
	@echo

$(COMP_NAME) : $(COMP_IMPL) $(COMP_SERVICES)
	@echo
	@echo --------------------------------------------------------------------------------
	@echo
	@echo The $(COMP_NAME) C++ component can be used with the UNO Executable.
	@echo See "http://wiki.openoffice.org/wiki/Documentation/DevGuide/WritingUNO/The_UNO_Executable"
	@echo
	@echo The Server.run target starts a remote SERVER.
	@echo Open another terminal and run the Client.run target to connect to that server.
	@echo
	@echo NOTE: The example uses the $(QM)uno$(QM) tool to prepare a working UNO environment.
	@echo $(SQM)      $(SQM)The example component is made available via -env:URE_MORE_SERVICES=...
	@echo $(SQM)      $(SQM)Please check the generated $(QM)$(COMP_SERVICES)$(QM)
	@echo $(SQM)      $(SQM)to see how you can specify your own components in such an environment
	@echo $(SQM)      $(SQM)and how to use the passive UNO registration.
	@echo
	@echo --------------------------------------------------------------------------------
	@echo


Server.run: $(COMP_IMPL)
	@echo
	@echo Start the remote SERVER process ...
	@echo -
ifeq "$(OS)" "WIN"
	@echo
	start uno \
		$(ENV_COMP_SERVICES) \
		--singleaccept \
		-s $(EXPORTED_COMPONENT) \
		-u "$(CONNECTION_URL)"
	@echo
else
	@echo
	uno \
		$(ENV_COMP_SERVICES) \
		--singleaccept \
		-s $(EXPORTED_COMPONENT) \
		-u "$(CONNECTION_URL)" &
	@echo
	@echo waiting on the server process ...
	@echo
	sleep 5
	@echo
endif
	@echo
	@echo -
	@echo Start CLIENT process ...
	@echo -
	uno \
		$(ENV_COMP_SERVICES) \
		-s $(SERVICE_NAME) \
		-- "$(CONNECTION_URL)"
	@echo -
	@echo

.PHONY: clean
clean :
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_COMP_INC))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_COMP_GEN))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_COMP_SLO))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_COMP_LIB))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_COMP_BIN))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_COMP_DEPLOY))

